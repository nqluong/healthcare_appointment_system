package project.healthcare_appointment.dto.response.user_profile_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import project.healthcare_appointment.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile response DTO")
public class ProfileResponse {
    @Schema(description = "Profile ID")
    UUID id;

    @Schema(description = "User's first name", example = "John")
    String firstName;

    @Schema(description = "User's last name", example = "Doe")
    String lastName;

    @Schema(description = "User's phone number", example = "+1234567890")
    String phone;

    @Schema(description = "User's date of birth", example = "1990-01-15")
    LocalDate dateOfBirth;

    @Schema(description = "User's gender")
    Gender gender;

    @Schema(description = "User's address", example = "123 Main St, City, State")
    String address;

    @Schema(description = "Avatar URL")
    String avatarUrl;

    @Schema(description = "Profile creation timestamp")
    LocalDateTime createdAt;

    @Schema(description = "Profile last update timestamp")
    LocalDateTime updatedAt;

    @Schema(description = "Username")
    String username;

    @Schema(description = "Email address")
    String email;

    @Schema(description = "User role")
    String role;

    @Schema(description = "Account active status")
    Boolean isActive;
}
