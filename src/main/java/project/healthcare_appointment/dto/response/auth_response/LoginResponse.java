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
@Schema(description = "Login response with tokens and user info")
public class LoginResponse {
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzUxMiJ9...")
    String accessToken;

    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzUxMiJ9...")
    String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    String tokenType = "Bearer";

    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID userId;

    @Schema(description = "Doctor ID (if role is DOCTOR)", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
    UUID doctorId;

    @Schema(description = "Patient ID (if role is PATIENT)", example = "6f5e4d3c-2b1a-0d9c-8b7a-6e5f4d3c2b1a")
    UUID patientId;

    @Schema(description = "Username", example = "nqluong")
    String username;

    @Schema(description = "Email", example = "nqluong@gmail.com")
    String email;

    @Schema(description = "User role", example = "PATIENT")
    String role;

    @Schema(description = "First name", example = "Luong")
    String firstName;

    @Schema(description = "Last name", example = "Nguyen")
    String lastName;
}
