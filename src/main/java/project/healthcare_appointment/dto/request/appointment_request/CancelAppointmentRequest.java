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
@Schema(description = "Request to cancel an existing appointment")
public class CancelAppointmentRequest {
    @Schema(
            description = "UUID of the appointment to cancel",
            required = true, example = "123e4567-e89b-12d3-a456-426614174002"
    )
    @NotNull(message = "Appointment ID is required")
    UUID appointmentId;

    @Schema(
            description = "UUID of the patient who owns the appointment",
            required = true, example = "123e4567-e89b-12d3-a456-426614174001"
    )
    @NotNull(message = "Patient ID is required")
    UUID patientId;

    @Schema(
            description = "Reason for cancelling the appointment",
            maxLength = 500, example = "Personal emergency - need to reschedule"
    )
    @Size(max = 500, message = "Cancellation reason cannot exceed 500 characters")
    String cancellationReason;
}
