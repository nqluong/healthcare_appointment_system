package project.healthcare_appointment.dto.request.specialty_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request DTO for creating a new specialty")
public class CreateSpecialtyRequest {
    @NotBlank(message = "Specialty name is required")
    @Size(max = 100, message = "Specialty name must not exceed 100 characters")
    @Schema(description = "Name of the medical specialty", example = "Cardiology")
    String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Description of the specialty", example = "Medical specialty dealing with heart and cardiovascular system")
    String description;
}
