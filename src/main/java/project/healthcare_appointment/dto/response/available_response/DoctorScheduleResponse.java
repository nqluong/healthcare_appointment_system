package project.healthcare_appointment.dto.response.available_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Doctor schedule response")
public class DoctorScheduleResponse {
    @Schema(description = "Schedule ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id;

    @Schema(description = "Day of week (1=Monday, 7=Sunday)", example = "1")
    Integer dayOfWeek;

    @Schema(description = "Start time", example = "08:00")
    LocalTime startTime;

    @Schema(description = "End time", example = "12:00")
    LocalTime endTime;

    @Schema(description = "Slot duration in minutes", example = "30")
    Integer slotDuration;

    @Schema(description = "Whether schedule is active", example = "true")
    Boolean isActive;
}
