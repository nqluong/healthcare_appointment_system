package project.healthcare_appointment.dto.request.schedule_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to add available slots")
public class AddAvailableSlotRequest {
    @NotNull(message = "Slot date is required")
    @Future(message = "Slot date must be in the future")
    @Schema(description = "Date for the available slot", example = "2025-07-30")
    LocalDate slotDate;

    @NotNull(message = "Start time is required")
    @Schema(description = "Start time of the slot", example = "09:00")
    LocalTime startTime;

    @NotNull(message = "End time is required")
    @Schema(description = "End time of the slot", example = "11:00")
    LocalTime endTime;

    @Schema(description = "Slot duration in minutes (default: 30)", example = "30")
    Integer slotDurationMinutes = 30;
}
