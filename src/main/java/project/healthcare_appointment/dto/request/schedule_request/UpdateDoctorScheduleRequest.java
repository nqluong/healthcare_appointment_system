package project.healthcare_appointment.dto.request.schedule_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to update doctor schedule")
public class UpdateDoctorScheduleRequest {
    @Min(value = 1, message = "Day of week must be between 1 and 7")
    @Max(value = 7, message = "Day of week must be between 1 and 7")
    @Schema(description = "Day of week (1=Monday, 7=Sunday)", example = "1")
    Integer dayOfWeek;

    @Schema(description = "Start time", example = "08:00")
    LocalTime startTime;

    @Schema(description = "End time", example = "12:00")
    LocalTime endTime;

    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    @Max(value = 240, message = "Slot duration must not exceed 240 minutes")
    @Schema(description = "Slot duration in minutes", example = "30")
    Integer slotDuration;

    @Schema(description = "Whether schedule is active", example = "true")
    Boolean isActive;
}
