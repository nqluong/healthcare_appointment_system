package project.healthcare_appointment.dto.request.schedule_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to generate slots from schedules")
public class GenerateSlotsRequest {
    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    @Schema(description = "Start date for slot generation", example = "2025-07-28")
    LocalDate startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "End date for slot generation", example = "2025-08-03")
    LocalDate endDate;

    @Schema(description = "Override existing slots if they exist", example = "false")
    Boolean overrideExisting = false;
}
