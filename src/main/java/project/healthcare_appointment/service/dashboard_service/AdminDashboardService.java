package project.healthcare_appointment.service.dashboard_service;

import project.healthcare_appointment.dto.response.dashboard_response.BookingStatusStatsDTO;
import project.healthcare_appointment.dto.response.dashboard_response.DashboardStatsDTO;
import project.healthcare_appointment.dto.response.dashboard_response.SpecialtyStatsDTO;

import java.time.LocalDate;
import java.util.List;

public interface AdminDashboardService {

    DashboardStatsDTO getDashboardStats();

    Long getTotalDoctors();

    Long getApprovedDoctorsCount();

    Long getTotalPatients();

    Long getTotalAppointments();

    List<BookingStatusStatsDTO> getAppointmentsByStatus();

    List<BookingStatusStatsDTO> getAppointmentsByStatusAndDateRange(LocalDate startDate, LocalDate endDate);

    List<SpecialtyStatsDTO> getSpecialtyStats();
}
