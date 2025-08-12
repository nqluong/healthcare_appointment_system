package project.healthcare_appointment.dto.request.appointment_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to reject an appointment")
public class RejectAppointmentRequest {
    @Schema(
            description = "Reason for rejecting the appointment",
            required = true,
            maxLength = 500,
            example = "Doctor not available due to emergency. Please reschedule."
    )
    @NotBlank(message = "Rejection reason is required")
    @Size(max = 500, message = "Rejection reason cannot exceed 500 characters")
    String rejectionReason;
}
