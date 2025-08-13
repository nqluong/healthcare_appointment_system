package project.healthcare_appointment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.healthcare_appointment.dto.response.dashboard_response.BookingStatusStatsDTO;
import project.healthcare_appointment.dto.response.dashboard_response.DashboardStatsDTO;
import project.healthcare_appointment.dto.response.dashboard_response.SpecialtyStatsDTO;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.service.dashboard_service.AdminDashboardService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Admin dashboard statistics and analytics APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    @Operation(
            summary = "Get dashboard statistics",
            description = "Retrieve general statistics including total doctors, patients, and appointments",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved dashboard statistics"),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        try {
            DashboardStatsDTO stats = adminDashboardService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_ERROR.DASHBOARD_DATA_ACCESS_ERROR);
        }
    }

    @GetMapping("/doctors/count")
    @Operation(
            summary = "Get total number of doctors",
            description = "Retrieve the total count of registered doctors in the system",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctor count"),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> getTotalDoctors() {
        Long totalDoctors = adminDashboardService.getTotalDoctors();
        return ResponseEntity.ok(totalDoctors);
    }

    @GetMapping("/doctors/approved/count")
    @Operation(
            summary = "Get approved doctors count",
            description = "Retrieve the count of approved doctors",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<Long> getApprovedDoctorsCount() {
        Long approvedDoctors = adminDashboardService.getApprovedDoctorsCount();
        return ResponseEntity.ok(approvedDoctors);
    }

    @GetMapping("/patients/count")
    @Operation(
            summary = "Get total number of patients",
            description = "Retrieve the total count of registered patients in the system",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved patient count"),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> getTotalPatients() {
        Long totalPatients = adminDashboardService.getTotalPatients();
        return ResponseEntity.ok(totalPatients);
    }

    @GetMapping("/appointments/count")
    @Operation(
            summary = "Get total appointments count",
            description = "Retrieve the total number of appointments in the system",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointment count"),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> getTotalAppointments() {
        Long totalAppointments = adminDashboardService.getTotalAppointments();
        return ResponseEntity.ok(totalAppointments);
    }

    @GetMapping("/appointments/status-stats")
    @Operation(
            summary = "Get appointment statistics by status",
            description = "Retrieve detailed statistics of appointments grouped by their status (PENDING, CONFIRMED, CANCELLED, REJECTED, COMPLETED)",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointment status statistics"),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<BookingStatusStatsDTO>> getAppointmentsByStatus() {
        List<BookingStatusStatsDTO> statusStats = adminDashboardService.getAppointmentsByStatus();
        return ResponseEntity.ok(statusStats);
    }

    @GetMapping("/appointments/status-stats/date-range")
    @Operation(
            summary = "Get appointment statistics by status within date range",
            description = "Retrieve appointment status statistics filtered by date range",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointment status statistics"),
            @ApiResponse(responseCode = "400", description = "Invalid date range parameters"),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<BookingStatusStatsDTO>> getAppointmentsByStatusAndDateRange(
            @Parameter(
                    description = "Start date (YYYY-MM-DD). Must be before or equal to end date and cannot be in the future.",
                    example = "2025-08-01",
                    required = true
            )
            @RequestParam
            @NotNull(message = "Start date is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(
                    description = "End date (YYYY-MM-DD). Must be after or equal to start date and cannot be in the future.",
                    example = "2025-08-31",
                    required = true
            )
            @RequestParam
            @NotNull(message = "End date is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {
        List<BookingStatusStatsDTO> statusStats = adminDashboardService.getAppointmentsByStatusAndDateRange(startDate, endDate);
        return ResponseEntity.ok(statusStats);
    }

    @GetMapping("/specialties/stats")
    @Operation(
            summary = "Get statistics by specialty",
            description = "Retrieve doctor count grouped by specialty",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved specialty statistics"),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SpecialtyStatsDTO>> getSpecialtyStats() {
        List<SpecialtyStatsDTO> specialtyStats = adminDashboardService.getSpecialtyStats();
        return ResponseEntity.ok(specialtyStats);
    }

}
