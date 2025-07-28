package project.healthcare_appointment.service.appointment_service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.healthcare_appointment.dto.request.appointment_request.BookAppointmentRequest;
import project.healthcare_appointment.dto.request.appointment_request.CancelAppointmentRequest;
import project.healthcare_appointment.dto.response.PageResponse;
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
    @PreAuthorize("hasRole('PATIENT')  or hasRole('ADMIN')")
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
    @PreAuthorize("(hasRole('PATIENT') and @userSecurity.isPatientOwer(#patientId)) or hasRole('ADMIN')")
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
    @Transactional(readOnly = true)
    @PreAuthorize("(hasRole('PATIENT') and @userSecurity.isPatientOwer(#patientId)) or hasRole('ADMIN')")
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
}
