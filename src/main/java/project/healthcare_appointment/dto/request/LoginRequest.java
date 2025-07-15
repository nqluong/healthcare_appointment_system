package project.healthcare_appointment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Login request payload")
public class LoginRequest {
    @Schema(description = "Username", example = "nqluong", required = true)
    @NotBlank(message = "Username is required")
    String username;

    @Schema(description = "Password", example = "password123", required = true)
    @NotBlank(message = "Password is required")
    String password;
}
