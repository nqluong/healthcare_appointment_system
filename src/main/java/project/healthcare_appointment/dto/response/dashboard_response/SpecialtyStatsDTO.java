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
@Schema(description = "Statistics grouped by medical specialty")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecialtyStatsDTO {

    @Schema(description = "Specialty name", example = "Cardiology")
    String specialtyName;

    @Schema(description = "Number of doctors in this specialty", example = "25")
    Long doctorCount;

    @Schema(description = "Percentage of total doctors", example = "16.7")
    Double percentage;
}
