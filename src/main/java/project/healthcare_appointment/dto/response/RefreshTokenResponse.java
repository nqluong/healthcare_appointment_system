package project.healthcare_appointment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Refresh token request")
public class RefreshTokenResponse {
    @Schema(description = "New access token", example = "eyJhbGciOiJIUzUxMiJ9...")
    @NotBlank(message = "Refresh token is required")
    String accessToken;

    @Schema(description = "New refresh token", example = "eyJhbGciOiJIUzUxMiJ9...")
    @NotBlank(message = "Refresh token is required")
    String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    String tokenType = "Bearer";
}
