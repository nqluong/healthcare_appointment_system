package project.healthcare_appointment.dto.response.dashboard_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Dashboard statistics summary")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardStatsDTO {

    @Schema(description = "Total number of doctors", example = "150")
    Long totalDoctors;

    @Schema(description = "Number of approved doctors", example = "120")
    Long approvedDoctors;

    @Schema(description = "Total number of patients", example = "2500")
    Long totalPatients;

    @Schema(description = "Total number of appointments", example = "5000")
    Long totalAppointments;

    @Schema(description = "Number of pending appointments", example = "25")
    Long pendingAppointments;

    @Schema(description = "Number of confirmed appointments", example = "45")
    Long confirmedAppointments;

    @Schema(description = "Number of completed appointments", example = "4800")
    Long completedAppointments;

    @Schema(description = "Number of cancelled appointments", example = "100")
    Long cancelledAppointments;

    @Schema(description = "Number of rejected appointments", example = "30")
    Long rejectedAppointments;
}
