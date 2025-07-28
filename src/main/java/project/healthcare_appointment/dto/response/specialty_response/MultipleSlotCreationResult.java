package project.healthcare_appointment.dto.response.specialty_response;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import project.healthcare_appointment.dto.response.available_response.AvailableSlotResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Multiple slots creation result")
public class MultipleSlotCreationResult {
    @Schema(description = "Number of slots created", example = "4")
    Integer slotsCreated;

    @Schema(description = "Slot date", example = "2025-07-30")
    LocalDate slotDate;

    @Schema(description = "Original start time", example = "09:00")
    LocalTime originalStartTime;

    @Schema(description = "Original end time", example = "11:00")
    LocalTime originalEndTime;

    @Schema(description = "Slot duration used", example = "30")
    Integer slotDuration;

    @Schema(description = "Created slots")
    List<AvailableSlotResponse> createdSlots;

    @Schema(description = "Skipped time periods (already existed)")
    List<String> skippedPeriods;
}
