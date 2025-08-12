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
import project.healthcare_appointment.repository.AppointmentRepository;
import project.healthcare_appointment.repository.DoctorRepository;
import project.healthcare_appointment.repository.PatientRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    DoctorRepository doctorRepository;
    PatientRepository patientRepository;
    AppointmentRepository appointmentRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStatsDTO getDashboardStats() {
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
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTotalDoctors() {
        return doctorRepository.count();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long getApprovedDoctorsCount() {
        return doctorRepository.countByIsApproved(true);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTotalPatients() {
        return patientRepository.count();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTotalAppointments() {
        return appointmentRepository.count();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingStatusStatsDTO> getAppointmentsByStatus() {
        List<Object[]> results = appointmentRepository.getAppointmentCountByStatus();
        Long totalAppointments = appointmentRepository.count();

        return results.stream()
                .map(result -> {
                    String status = (String) result[0].toString();
                    Long count = (Long) result[1];
                    Double percentage = totalAppointments > 0 ? (count * 100.0) / totalAppointments : 0.0;

                    return BookingStatusStatsDTO.builder()
                            .status(status)
                            .count(count)
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingStatusStatsDTO> getAppointmentsByStatusAndDateRange(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = appointmentRepository.getAppointmentCountByStatusAndDateRange(startDate, endDate);
        Long totalAppointments = appointmentRepository.countByAppointmentDateBetween(startDate, endDate);

        return results.stream()
                .map(result -> {
                    String status = (String) result[0].toString();
                    Long count = (Long) result[1];
                    Double percentage = totalAppointments > 0 ? (count * 100.0) / totalAppointments : 0.0;

                    return BookingStatusStatsDTO.builder()
                            .status(status)
                            .count(count)
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<SpecialtyStatsDTO> getSpecialtyStats() {
        List<Object[]> results = doctorRepository.getDoctorCountBySpecialty();
        Long totalDoctors = doctorRepository.count();

        return results.stream()
                .map(result -> {
                    String specialtyName = (String) result[0];
                    Long count = (Long) result[1];
                    Double percentage = totalDoctors > 0 ? (count * 100.0) / totalDoctors : 0.0;

                    return SpecialtyStatsDTO.builder()
                            .specialtyName(specialtyName)
                            .doctorCount(count)
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .toList();
    }

}
