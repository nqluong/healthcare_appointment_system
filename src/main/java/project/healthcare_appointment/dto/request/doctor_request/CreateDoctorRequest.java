package project.healthcare_appointment.dto.request.doctor_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request DTO for creating a new doctor")
public class CreateDoctorRequest {
    @NotNull(message = "User ID is required")
    @Schema(description = "User ID to associate with the doctor", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID userId;

    @NotBlank(message = "License number is required")
    @Size(max = 50, message = "License number must not exceed 50 characters")
    @Schema(description = "Doctor's medical license number", example = "MD12345")
    String licenseNumber;

    @Min(value = 0, message = "Years of experience must be non-negative")
    @Schema(description = "Years of medical experience", example = "5")
    Integer yearsOfExperience = 0;

    @Size(max = 1000, message = "Qualification must not exceed 1000 characters")
    @Schema(description = "Doctor's qualifications and certifications", example = "MBBS, MD - Internal Medicine")
    String qualification;

    @DecimalMin(value = "0.0", inclusive = false, message = "Consultation fee must be positive")
    @Schema(description = "Doctor's consultation fee", example = "500000.00")
    BigDecimal consultationFee = BigDecimal.ZERO;

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    @Schema(description = "Doctor's biography", example = "Experienced cardiologist with 5 years of practice...")
    String bio;

    @Schema(description = "Specialty ID", example = "123e4567-e89b-12d3-a456-426614174001")
    UUID specialtyId;
}
