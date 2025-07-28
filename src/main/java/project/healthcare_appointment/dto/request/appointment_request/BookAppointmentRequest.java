package project.healthcare_appointment.dto.request.appointment_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to book a new appointment")
public class BookAppointmentRequest {
    @Schema(
            description = "UUID of the available doctor slot to book",
            required = true, example = "123e4567-e89b-12d3-a456-426614174000"
    )
    @NotNull(message = "Slot ID is required")
    UUID slotId;

    @Schema(
            description = "UUID of the patient booking the appointment",
            required = true, example = "123e4567-e89b-12d3-a456-426614174001"
    )
    @NotNull(message = "Patient ID is required")
    UUID patientId;

    @Schema(
            description = "Reason for the appointment visit",
            maxLength = 500, example = "Regular health checkup and consultation"
    )
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    String reason;

    @Schema(
            description = "Additional notes or special requirements for the appointment",
            maxLength = 1000, example = "Patient has been experiencing back pain for the past week"
    )
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes;
}
