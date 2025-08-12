package project.healthcare_appointment.dto.response.appointment_response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "Response for appointment approval/rejection actions")
public class AppointmentActionResponse {

    @Schema(description = "UUID of the appointment", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID appointmentId;

    @Schema(description = "Current status of the appointment", example = "CONFIRMED")
    AppointmentStatus status;

    @Schema(description = "Timestamp when the action was performed", example = "2024-01-15T10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime actionDate;

    @Schema(description = "Notes from doctor", example = "Appointment confirmed. Please arrive 15 minutes early.")
    String doctorNotes;

    @Schema(description = "Success message", example = "Appointment approved successfully")
    String message;
}
