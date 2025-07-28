package project.healthcare_appointment.dto.response.available_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Slot generation result")
public class SlotGenerationResult {
    @Schema(description = "Number of slots generated", example = "24")
    Integer slotsGenerated;

    @Schema(description = "Number of slots skipped (already existed)", example = "2")
    Integer slotsSkipped;

    @Schema(description = "Start date of generation", example = "2025-07-28")
    LocalDate startDate;

    @Schema(description = "End date of generation", example = "2025-08-03")
    LocalDate endDate;

    @Schema(description = "Generated slots")
    List<AvailableSlotResponse> generatedSlots;
}
