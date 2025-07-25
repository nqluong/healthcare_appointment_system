package project.healthcare_appointment.dto.response.specialty_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response DTO for specialty information")
public class SpecialtyResponse {

    @Schema(description = "Specialty's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id;

    @Schema(description = "Name of the specialty", example = "Cardiology")
    String name;

    @Schema(description = "Description of the specialty", example = "Medical specialty dealing with heart and cardiovascular system")
    String description;

    @Schema(description = "Specialty active status", example = "true")
    Boolean isActive;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
    LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00")
    LocalDateTime updatedAt;

    @Schema(description = "Number of doctors in this specialty", example = "12")
    Long doctorCount;
}
