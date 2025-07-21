package project.healthcare_appointment.dto.response.auth_response;

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
@Schema(description = "Registration response")
public class RegisterResponse {
    @Schema(description = "Success message", example = "User registered successfully")
    String message;

    @Schema(description = "Created user ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID uuid;
}
