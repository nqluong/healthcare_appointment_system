package project.healthcare_appointment.dto.response.appointment_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import project.healthcare_appointment.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response containing details of a cancelled appointment")
public class CancelAppointmentResponse {

    @Schema(description = "Unique identifier of the cancelled appointment", example = "123e4567-e89b-12d3-a456-426614174002")
    UUID appointmentId;

    @Schema(description = "New status of the appointment after cancellation", example = "CANCELLED")
    AppointmentStatus status;

    @Schema(description = "Timestamp when the appointment was cancelled", example = "2024-12-01T16:45:00")
    LocalDateTime cancelledAt;

    @Schema(description = "Reason provided for cancelling the appointment", example = "Personal emergency")
    String cancellationReason;

    @Schema(description = "Success message confirming the cancellation", example = "Appointment cancelled successfully")
    String message;
}
