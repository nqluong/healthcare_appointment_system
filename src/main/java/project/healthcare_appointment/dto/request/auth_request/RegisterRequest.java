package project.healthcare_appointment.dto.request.auth_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import project.healthcare_appointment.enums.Gender;
import project.healthcare_appointment.enums.UserRole;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "User registration request")
public class RegisterRequest {
    @Schema(description = "Username (unique)", example = "nqluong", required = true)
    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must not exceed 100 characters")
    String username;

    @Schema(description = "Email address (unique)", example = "nqluong@gmail.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email;

    @Schema(description = "Password (minimum 6 characters)", example = "password123", required = true)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password;

    @Schema(description = "First name", example = "Luong", required = true)
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    String firstName;

    @Schema(description = "Last name", example = "Nguyen", required = true)
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    String lastName;

    @Schema(description = "Phone number", example = "0123456789")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone;

    @Schema(description = "Date of birth", example = "2004-05-14")
    LocalDate dateOfBirth;

    @Schema(description = "Gender", example = "MALE")
    Gender gender;

    @Schema(description = "User role", example = "PATIENT or DOCTOR", required = true)
    UserRole role;

    @Schema(description = "Address", example = "Hanoi, Vietnam")
    String address;

    // Patient-specific fields (only required if role is PATIENT)
    @Schema(description = "Medical history (required for patients)", example = "Bach Mai")
    String medicalHistory;

    @Schema(description = "Allergies (required for patients)", example = "Khong co")
    String allergies;

    @Schema(description = "Blood type", example = "A+")
    @Size(max = 5, message = "Blood type must not exceed 5 characters")
    String bloodType;

    @Schema(description = "Emergency contact name", example = "Nugyen")
    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    String emergencyContactName;

    @Schema(description = "Emergency contact phone", example = "0987654321")
    @Size(max = 20, message = "Emergency contact phone must not exceed 20 characters")
    String emergencyContactPhone;
}
