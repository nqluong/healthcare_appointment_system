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
@Schema(description = "Booking statistics grouped by status")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingStatusStatsDTO {

    @Schema(description = "Appointment status", example = "CONFIRMED", allowableValues = {"PENDING", "CONFIRMED", "CANCELLED", "REJECTED", "COMPLETED"})
    String status;

    @Schema(description = "Number of appointments with this status", example = "45")
    Long count;

    @Schema(description = "Percentage of total appointments", example = "15.5")
    Double percentage;
}
