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
public class RefreshTokenRequest {
    @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzUxMiJ9...", required = true)
    @NotBlank(message = "Refresh token is required")
    @NotBlank
    String refreshToken;
}
