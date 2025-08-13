package project.healthcare_appointment.service.dashboard_service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import project.healthcare_appointment.dto.response.dashboard_response.BookingStatusStatsDTO;
import project.healthcare_appointment.dto.response.dashboard_response.DashboardStatsDTO;
import project.healthcare_appointment.dto.response.dashboard_response.SpecialtyStatsDTO;
import project.healthcare_appointment.enums.AppointmentStatus;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.repository.AppointmentRepository;
import project.healthcare_appointment.repository.DoctorRepository;
import project.healthcare_appointment.repository.PatientRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    DoctorRepository doctorRepository;
    PatientRepository patientRepository;
    AppointmentRepository appointmentRepository;

    static final long MAX_DATE_RANGE_DAYS = 365;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStatsDTO getDashboardStats() {
        try{
            Long totalDoctors = doctorRepository.count();
            Long approvedDoctors = doctorRepository.countByIsApproved(true);
            Long totalPatients = patientRepository.count();
            Long totalAppointments = appointmentRepository.count();

            Long pendingAppointments = appointmentRepository.countByStatus(AppointmentStatus.PENDING);
            Long confirmedAppointments = appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED);
            Long completedAppointments = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
            Long cancelledAppointments = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);
            Long rejectedAppointments = appointmentRepository.countByStatus(AppointmentStatus.REJECTED);

            return DashboardStatsDTO.builder()
                    .totalDoctors(totalDoctors)
                    .approvedDoctors(approvedDoctors)
                    .totalPatients(totalPatients)
                    .totalAppointments(totalAppointments)
                    .pendingAppointments(pendingAppointments)
                    .confirmedAppointments(confirmedAppointments)
                    .completedAppointments(completedAppointments)
                    .cancelledAppointments(cancelledAppointments)
                    .rejectedAppointments(rejectedAppointments)
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.DASHBOARD_DATA_ACCESS_ERROR);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTotalDoctors() {
        return getTotalDoctorsInternal();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long getApprovedDoctorsCount() {
        return doctorRepository.countByIsApproved(true);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTotalPatients() {
        return getTotalPatientsInternal();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTotalAppointments() {
        return getTotalAppointmentsInternal();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingStatusStatsDTO> getAppointmentsByStatus() {
        try {
            List<Object[]> results = appointmentRepository.getAppointmentCountByStatus();
            Long totalAppointments = getTotalAppointmentsInternal();

            List<BookingStatusStatsDTO> stats = results.stream()
                    .map(result -> createBookingStatusStatsDTO(result, totalAppointments))
                    .toList();

            return stats;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.APPOINTMENT_STATUS_STATS_ERROR);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingStatusStatsDTO> getAppointmentsByStatusAndDateRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        try {
            List<Object[]> results = appointmentRepository.getAppointmentCountByStatusAndDateRange(startDate, endDate);
            Long totalAppointments = appointmentRepository.countByAppointmentDateBetween(startDate, endDate);

            List<BookingStatusStatsDTO> stats = results.stream()
                    .map(result -> createBookingStatusStatsDTO(result, totalAppointments))
                    .toList();

            return stats;

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.APPOINTMENT_STATUS_STATS_ERROR);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<SpecialtyStatsDTO> getSpecialtyStats() {
        try {
            List<Object[]> results = doctorRepository.getDoctorCountBySpecialty();
            Long totalDoctors = getTotalDoctorsInternal();

            List<SpecialtyStatsDTO> stats = results.stream()
                    .map(result -> createSpecialtyStatsDTO(result, totalDoctors))
                    .toList();
            return stats;

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.SPECIALTY_STATS_ERROR);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        if (startDate.isAfter(endDate)) {
            log.warn("Invalid date range: start date {} is after end date {}", startDate, endDate);
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            log.warn("Future dates not allowed: start date {}, end date {}", startDate, endDate);
            throw new AppException(ErrorCode.FUTURE_DATE_NOT_ALLOWED);
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > MAX_DATE_RANGE_DAYS) {
            throw new AppException(ErrorCode.DATE_RANGE_TOO_LARGE);
        }
    }

    private BookingStatusStatsDTO createBookingStatusStatsDTO(Object[] result, Long totalCount) {
        try {
            String status = result[0].toString();
            Long count = (Long) result[1];
            Double percentage = calculatePercentage(count, totalCount);

            return BookingStatusStatsDTO.builder()
                    .status(status)
                    .count(count)
                    .percentage(percentage)
                    .build();

        } catch (Exception e) {
            throw new AppException(ErrorCode.STATISTICS_CALCULATION_ERROR);
        }
    }

    private SpecialtyStatsDTO createSpecialtyStatsDTO(Object[] result, Long totalCount) {
        try {
            String specialtyName = (String) result[0];
            Long count = (Long) result[1];
            Double percentage = calculatePercentage(count, totalCount);

            return SpecialtyStatsDTO.builder()
                    .specialtyName(specialtyName)
                    .doctorCount(count)
                    .percentage(percentage)
                    .build();

        } catch (Exception e) {
            throw new AppException(ErrorCode.STATISTICS_CALCULATION_ERROR);
        }
    }

    private Double calculatePercentage(Long count, Long total) {
        if (total == null || total == 0) {
            return 0.0;
        }

        if (count == null) {
            count = 0L;
        }

        try {
            double percentage = (count * 100.0) / total;
            return Math.round(percentage * 100.0) / 100.0;
        } catch (ArithmeticException e) {
            throw new AppException(ErrorCode.PERCENTAGE_CALCULATION_ERROR);
        }
    }

    private Long getTotalDoctorsInternal() {
        try {
            Long count = doctorRepository.count();
            return count;
        } catch (Exception e) {
            throw new AppException(ErrorCode.DOCTOR_COUNT_RETRIEVAL_ERROR);
        }
    }

    private Long getTotalPatientsInternal() {
        try {
            Long count = patientRepository.count();
            return count;
        } catch (Exception e) {
            throw new AppException(ErrorCode.PATIENT_COUNT_RETRIEVAL_ERROR);
        }
    }

    private Long getTotalAppointmentsInternal() {
        try {
            Long count = appointmentRepository.count();
            return count;
        } catch (Exception e) {
            throw new AppException(ErrorCode.APPOINTMENT_COUNT_RETRIEVAL_ERROR);
        }
    }
}
