package project.healthcare_appointment.dto.request.user_profile_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import project.healthcare_appointment.enums.Gender;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request DTO for updating user profile")
public class UpdateProfileRequest {
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(description = "User's first name", example = "John", required = true)
    String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(description = "User's last name", example = "Doe", required = true)
    String lastName;

    @Pattern(regexp = "^[+]?[0-9\\-\\s]{10,15}$", message = "Invalid phone number format")
    @Schema(description = "User's phone number", example = "+1234567890")
    String phone;

    @Past(message = "Date of birth must be in the past")
    @Schema(description = "User's date of birth", example = "1990-01-15")
    LocalDate dateOfBirth;

    @Schema(description = "User's gender")
    Gender gender;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Schema(description = "User's address", example = "123 Main St, City, State")
    String address;
}
