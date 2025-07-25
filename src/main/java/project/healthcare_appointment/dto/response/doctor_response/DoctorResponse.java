package project.healthcare_appointment.dto.response.doctor_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import project.healthcare_appointment.dto.response.specialty_response.SpecialtyBasicResponse;
import project.healthcare_appointment.dto.response.user_profile_response.UserBasicResponse;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response DTO for doctor information")
public class DoctorResponse {
    @Schema(description = "Doctor's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id;

    @Schema(description = "Doctor's medical license number", example = "MD12345")
    String licenseNumber;

    @Schema(description = "Years of medical experience", example = "5")
    Integer yearsOfExperience;

    @Schema(description = "Doctor's qualifications", example = "MBBS, MD - Internal Medicine")
    String qualification;

    @Schema(description = "Doctor's consultation fee", example = "150.00")
    BigDecimal consultationFee;

    @Schema(description = "Doctor's biography", example = "Experienced cardiologist...")
    String bio;

    @Schema(description = "Doctor approval status", example = "true")
    Boolean isApproved;

    @Schema(description = "Associated user information")
    UserBasicResponse user;

    @Schema(description = "Doctor's specialty information")
    SpecialtyBasicResponse specialty;
}
