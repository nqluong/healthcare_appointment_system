package project.healthcare_appointment.dto.response.available_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Available slot response")
public class AvailableSlotResponse {
    @Schema(description = "Slot ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id;

    @Schema(description = "Slot date", example = "2025-07-30")
    LocalDate slotDate;

    @Schema(description = "Start time", example = "09:00")
    LocalTime startTime;

    @Schema(description = "End time", example = "09:30")
    LocalTime endTime;

    @Schema(description = "Whether slot is available", example = "true")
    Boolean isAvailable;

    @Schema(description = "Creation timestamp", example = "2025-07-25T09:00:00")
    LocalDateTime createdAt;

    @Schema(description = "Update timestamp", example = "2025-07-25T10:30:00")
    LocalDateTime updatedAt;
}
