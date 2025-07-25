package project.healthcare_appointment.dto.response.specialty_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Basic specialty information for doctor response")
public class SpecialtyBasicResponse {

    @Schema(description = "Specialty's unique identifier")
    UUID id;

    @Schema(description = "Name of the specialty", example = "Cardiology")
    String name;

    @Schema(description = "Specialty active status", example = "true")
    Boolean isActive;
}
