package project.healthcare_appointment.dto.request.schedule_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to create or update doctor schedule")
public class CreateDoctorScheduleRequest {
    @NotNull(message = "Day of week is required")
    @Min(value = 1, message = "Day of week must be between 1 and 7")
    @Max(value = 7, message = "Day of week must be between 1 and 7")
    @Schema(description = "Day of week (1=Monday, 7=Sunday)", example = "1")
    Integer dayOfWeek;

    @NotNull(message = "Start time is required")
    @Schema(description = "Start time", example = "08:00")
    LocalTime startTime;

    @NotNull(message = "End time is required")
    @Schema(description = "End time", example = "12:00")
    LocalTime endTime;

    @NotNull(message = "Slot duration is required")
    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    @Max(value = 240, message = "Slot duration must not exceed 240 minutes")
    @Schema(description = "Slot duration in minutes", example = "30")
    Integer slotDuration;

    @Schema(description = "Whether schedule is active", example = "true")
    Boolean isActive = true;
}
