package project.healthcare_appointment.service.appointment_service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.healthcare_appointment.dto.request.appointment_request.*;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.appointment_response.AppointmentActionResponse;
import project.healthcare_appointment.dto.response.appointment_response.AppointmentSummaryResponse;
import project.healthcare_appointment.dto.response.appointment_response.BookAppointmentResponse;
import project.healthcare_appointment.dto.response.appointment_response.CancelAppointmentResponse;
import project.healthcare_appointment.enums.AppointmentStatus;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.mapper.AppointmentMapper;
import project.healthcare_appointment.model.Appointment;
import project.healthcare_appointment.model.DoctorAvailableSlot;
import project.healthcare_appointment.model.Patient;
import project.healthcare_appointment.repository.AppointmentRepository;
import project.healthcare_appointment.repository.DoctorAvailableSlotRepository;
import project.healthcare_appointment.repository.DoctorRepository;
import project.healthcare_appointment.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentServiceImpl implements AppointmentService{

    AppointmentRepository appointmentRepository;
    AppointmentMapper appointmentMapper;
    DoctorAvailableSlotRepository doctorAvailableSlotRepository;
    PatientRepository patientRepository;
    DoctorRepository doctorRepository;

    @Override
    @PreAuthorize("(hasRole('PATIENT') and @userSecurity.isPatientOwner(#request.patientId)) or hasRole('ADMIN')")
    public BookAppointmentResponse bookAppointment(BookAppointmentRequest request) {
        log.info("Booking appointment for patient {} on slot {}", request.getPatientId(), request.getSlotId());

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        DoctorAvailableSlot slot = doctorAvailableSlotRepository.findAvailableSlotById(request.getSlotId())
                .orElseThrow(() -> new AppException(ErrorCode.SLOT_NOT_AVAILABLE));

        // Check if slot is still valid (not in the past)
        LocalDateTime slotDateTime = LocalDateTime.of(slot.getSlotDate(), slot.getStartTime());
        if (slotDateTime.isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.SLOT_EXPIRED);
        }

        List<Appointment> conflictingAppointments = appointmentRepository.findConflictingAppointments(
                request.getPatientId(), slot.getSlotDate(), slot.getStartTime(), slot.getEndTime());

        if (!conflictingAppointments.isEmpty()) {
            throw new AppException(ErrorCode.DUPLICATE_APPOINTMENT);
        }

        Appointment appointment = Appointment.builder()
                .appointmentDate(slot.getSlotDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(AppointmentStatus.PENDING)
                .reason(request.getReason())
                .notes(request.getNotes())
                .consultationFee(slot.getDoctor().getConsultationFee())
                .patient(patient)
                .doctor(slot.getDoctor())
                .build();

        appointment = appointmentRepository.save(appointment);

        slot.setIsAvailable(false);
        doctorAvailableSlotRepository.save(slot);

        log.info("Successfully booked appointment {} for patient {}", appointment.getId(), request.getPatientId());

        return appointmentMapper.toBookResponse(appointment);
    }

    @Override
    @PreAuthorize("(hasRole('PATIENT') and @userSecurity.isPatientOwner(#request.patientId)) or hasRole('ADMIN')")
    public CancelAppointmentResponse cancelAppointment(CancelAppointmentRequest request) {
        log.info("Cancelling appointment {} for patient {}", request.getAppointmentId(), request.getPatientId());

        Appointment appointment = appointmentRepository.findByIdAndPatientId(
                        request.getAppointmentId(), request.getPatientId())
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        // Validate appointment status
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AppException(ErrorCode.APPOINTMENT_ALREADY_CANCELLED);
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppException(ErrorCode.APPOINTMENT_ALREADY_COMPLETED);
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING &&
                appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new AppException(ErrorCode.APPOINTMENT_NOT_CANCELLABLE);
        }

        // Check if cancellation is within allowed time (1 day before)
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime());
        LocalDateTime oneDayBefore = appointmentDateTime.minusDays(1);

        if (LocalDateTime.now().isAfter(oneDayBefore)) {
            throw new AppException(ErrorCode.CANCELLATION_TOO_LATE);
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setDoctorNotes(request.getCancellationReason());

        appointment = appointmentRepository.save(appointment);

        Optional<DoctorAvailableSlot> slotOpt = doctorAvailableSlotRepository.findAvailableSlot(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                appointment.getStartTime());

        slotOpt.ifPresent(slot -> {
            slot.setIsAvailable(true);
            doctorAvailableSlotRepository.save(slot);
        });

        log.info("Successfully cancelled appointment {} for patient {}", request.getAppointmentId(), request.getPatientId());

        return CancelAppointmentResponse.builder()
                .appointmentId(appointment.getId())
                .status(appointment.getStatus())
                .cancelledAt(appointment.getUpdatedAt())
                .cancellationReason(request.getCancellationReason())
                .message("Appointment cancelled successfully")
                .build();
    }

    @Override
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwnerOfAppointment(#appointmentId)) or hasRole('ADMIN')")
    public AppointmentActionResponse approveAppointment(UUID appointmentId, ApproveAppointmentRequest request) {
        validateApproveRequest(request);

        Appointment appointment = findAppointmentById(appointmentId);
        validateAppointmentForApproval(appointment);

        try {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointment.setDoctorNotes(request.getDoctorNotes());
            appointment = appointmentRepository.save(appointment);

            return buildAppointmentActionResponse(appointment, request.getDoctorNotes(),
                    "Appointment approved successfully");

        } catch (Exception e) {
            log.error("Failed to approve appointment {}: {}", appointmentId, e.getMessage());
            throw new AppException(ErrorCode.APPOINTMENT_UPDATE_FAILED);
        }
    }

    @Override
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwnerOfAppointment(#appointmentId)) or hasRole('ADMIN')")
    public AppointmentActionResponse rejectAppointment(UUID appointmentId, RejectAppointmentRequest request) {

        validateRejectRequest(request);

        Appointment appointment = findAppointmentById(appointmentId);
        validateAppointmentForRejection(appointment);

        try {
            appointment.setStatus(AppointmentStatus.REJECTED);
            appointment.setDoctorNotes(request.getRejectionReason());
            appointment = appointmentRepository.save(appointment);

            updateDoctorSlotAvailability(appointment, true);

            log.info("Successfully rejected appointment {} by doctor", appointmentId);

            return buildAppointmentActionResponse(appointment, request.getRejectionReason(),
                    "Appointment rejected successfully");

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to reject appointment {}: {}", appointmentId, e.getMessage());
            throw new AppException(ErrorCode.APPOINTMENT_UPDATE_FAILED);
        }
    }

    private Appointment findAppointmentById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("(hasRole('PATIENT') and @userSecurity.isPatientOwner(#patientId)) or hasRole('ADMIN')")
    public PageResponse<AppointmentSummaryResponse> getPatientAppointments(UUID patientId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateDescStartTimeDesc(patientId, pageable);
        return appointmentMapper.toResponsePage(appointments);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwner(#doctorId)) or hasRole('ADMIN')")
    public PageResponse<AppointmentSummaryResponse> getDoctorAppointments(UUID doctorId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByDoctorIdOrderByAppointmentDateDescStartTimeDesc(doctorId, pageable);
        return appointmentMapper.toResponsePage(appointments);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<AppointmentSummaryResponse> getAllAppointments(Pageable pageable,
                                                                       AppointmentFilterRequest filterRequest) {

        validateFilterRequest(filterRequest);
        log.info("Admin retrieving all appointments with filters: {}", filterRequest);

        try {
            Page<Appointment> appointments;

            // Check if name filters are used
            boolean hasNameFilters = hasNameFilters(filterRequest);

            if (hasNameFilters) {
                appointments = appointmentRepository.findAppointmentsWithAllFilters(
                        filterRequest.getStatus(),
                        filterRequest.getDoctorId(),
                        filterRequest.getPatientId(),
                        filterRequest.getStartDate(),
                        filterRequest.getEndDate(),
                        filterRequest.getDoctorName(),
                        filterRequest.getPatientName(),
                        pageable
                );
            } else {

                appointments = appointmentRepository.findAppointmentsWithBasicFilters(
                        filterRequest.getStatus(),
                        filterRequest.getDoctorId(),
                        filterRequest.getPatientId(),
                        filterRequest.getStartDate(),
                        filterRequest.getEndDate(),
                        pageable
                );
            }

            return appointmentMapper.toResponsePage(appointments);

        } catch (Exception e) {
            log.error("Failed to retrieve appointments with filters: {}", e.getMessage());
            throw new AppException(ErrorCode.APPOINTMENT_FILTER_INVALID);
        }
    }

    private void validateAppointmentForApproval(Appointment appointment) {
        AppointmentStatus status = appointment.getStatus();

        switch (status) {
            case CONFIRMED:
                throw new AppException(ErrorCode.APPOINTMENT_ALREADY_APPROVED);
            case REJECTED:
                throw new AppException(ErrorCode.APPOINTMENT_ALREADY_REJECTED);
            case CANCELLED:
                throw new AppException(ErrorCode.APPOINTMENT_ALREADY_CANCELLED);
            case PENDING:
                // Valid status for approval
                break;
            default:
                throw new AppException(ErrorCode.INVALID_APPOINTMENT_STATUS);
        }

        // Check if appointment is not in the past
        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getStartTime()
        );

        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.APPOINTMENT_EXPIRED);
        }
    }

    private void validateAppointmentForRejection(Appointment appointment) {
        AppointmentStatus status = appointment.getStatus();

        switch (status) {
            case CONFIRMED:
                throw new AppException(ErrorCode.APPOINTMENT_ALREADY_APPROVED);
            case REJECTED:
                throw new AppException(ErrorCode.APPOINTMENT_ALREADY_REJECTED);
            case CANCELLED:
                throw new AppException(ErrorCode.APPOINTMENT_ALREADY_CANCELLED);
            case PENDING:
                break;
            default:
                throw new AppException(ErrorCode.INVALID_APPOINTMENT_STATUS);
        }
    }
    private void validateApproveRequest(ApproveAppointmentRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.APPOINTMENT_FILTER_INVALID);
        }
    }

    private void validateRejectRequest(RejectAppointmentRequest request) {
        if (request == null || request.getRejectionReason() == null ||
                request.getRejectionReason().trim().isEmpty()) {
            throw new AppException(ErrorCode.APPOINTMENT_FILTER_INVALID);
        }
    }

    private void validateFilterRequest(AppointmentFilterRequest filterRequest) {
        if (filterRequest == null) {
            return;
        }

        if (filterRequest.getStartDate() != null && filterRequest.getEndDate() != null) {
            if (filterRequest.getStartDate().isAfter(filterRequest.getEndDate())) {
                throw new AppException(ErrorCode.APPOINTMENT_DATE_INVALID);
            }
        }

        // Validate name filters
        if (filterRequest.getDoctorName() != null && filterRequest.getDoctorName().trim().length() < 2) {
            throw new AppException(ErrorCode.APPOINTMENT_FILTER_INVALID);
        }

        if (filterRequest.getPatientName() != null && filterRequest.getPatientName().trim().length() < 2) {
            throw new AppException(ErrorCode.APPOINTMENT_FILTER_INVALID);
        }
    }

    private void updateDoctorSlotAvailability(Appointment appointment, boolean isAvailable) {
        try {
            Optional<DoctorAvailableSlot> slotOpt = doctorAvailableSlotRepository.findAvailableSlot(
                    appointment.getDoctor().getId(),
                    appointment.getAppointmentDate(),
                    appointment.getStartTime());

            if (slotOpt.isPresent()) {
                DoctorAvailableSlot slot = slotOpt.get();
                slot.setIsAvailable(isAvailable);
                doctorAvailableSlotRepository.save(slot);
            } else {
                log.warn("Doctor slot not found for appointment {}", appointment.getId());
            }
        } catch (Exception e) {
            log.error("Failed to update slot availability for appointment {}: {}",
                    appointment.getId(), e.getMessage());
            throw new AppException(ErrorCode.SLOT_UPDATE_FAILED);
        }
    }

    private AppointmentActionResponse buildAppointmentActionResponse(Appointment appointment,
                                                                     String notes, String message) {
        return AppointmentActionResponse.builder()
                .appointmentId(appointment.getId())
                .status(appointment.getStatus())
                .actionDate(appointment.getUpdatedAt())
                .doctorNotes(notes)
                .message(message)
                .build();
    }

    private boolean hasNameFilters(AppointmentFilterRequest filterRequest) {
        return (filterRequest.getDoctorName() != null && !filterRequest.getDoctorName().trim().isEmpty()) ||
                (filterRequest.getPatientName() != null && !filterRequest.getPatientName().trim().isEmpty());
    }
}
