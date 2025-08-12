package project.healthcare_appointment.dto.request.appointment_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to approve an appointment")
public class ApproveAppointmentRequest {
    @Schema(
            description = "Optional notes from doctor about the approval",
            maxLength = 1000,
            example = "Patient condition reviewed. Appointment confirmed for regular checkup."
    )
    @Size(max = 1000, message = "Doctor notes cannot exceed 1000 characters")
    String doctorNotes;
}
