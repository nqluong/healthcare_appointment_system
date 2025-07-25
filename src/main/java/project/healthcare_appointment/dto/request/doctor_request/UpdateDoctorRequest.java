package project.healthcare_appointment.dto.request.doctor_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request DTO for updating doctor information")
public class UpdateDoctorRequest {
    @Size(max = 50, message = "License number must not exceed 50 characters")
    @Schema(description = "Doctor's medical license number", example = "MD12345")
    String licenseNumber;

    @Min(value = 0, message = "Years of experience must be non-negative")
    @Schema(description = "Years of medical experience", example = "6")
    Integer yearsOfExperience;

    @Size(max = 1000, message = "Qualification must not exceed 1000 characters")
    @Schema(description = "Doctor's qualifications and certifications", example = "MBBS, MD - Internal Medicine, Fellowship in Cardiology")
    String qualification;

    @DecimalMin(value = "0.0", inclusive = false, message = "Consultation fee must be positive")
    @Schema(description = "Doctor's consultation fee", example = "700000.00")
    BigDecimal consultationFee;

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    @Schema(description = "Doctor's biography", example = "Updated biography...")
    String bio;

    @Schema(description = "Specialty ID", example = "123e4567-e89b-12d3-a456-426614174001")
    UUID specialtyId;

    @Schema(description = "Doctor approval status", example = "true")
    Boolean isApproved;
}
