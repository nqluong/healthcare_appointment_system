package project.healthcare_appointment.dto.response.appointment_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import project.healthcare_appointment.enums.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response containing details of a successfully booked appointment")
public class BookAppointmentResponse {
    @Schema(description = "Unique identifier of the booked appointment", example = "123e4567-e89b-12d3-a456-426614174002")
    UUID appointmentId;

    @Schema(description = "Unique identifier of the doctor", example = "123e4567-e89b-12d3-a456-426614174003")
    UUID doctorId;

    @Schema(description = "Full name of the doctor", example = "Nguyen Luong")
    String doctorName;

    @Schema(description = "Unique identifier of the patient", example = "123e4567-e89b-12d3-a456-426614174001")
    UUID patientId;

    @Schema(description = "Full name of the patient", example = "Nugyen Van A")
    String patientName;

    @Schema(description = "Date of the appointment", example = "2024-12-15")
    LocalDate appointmentDate;

    @Schema(description = "Start time of the appointment", example = "10:00:00")
    LocalTime startTime;

    @Schema(description = "End time of the appointment", example = "10:30:00")
    LocalTime endTime;

    @Schema(description = "Status of the appointment (typically PENDING after booking)", example = "PENDING")
    AppointmentStatus status;

    @Schema(description = "Reason for the appointment", example = "Regular health checkup")
    String reason;

    @Schema(description = "Additional notes for the appointment", example = "Patient has been experiencing back pain")
    String notes;

    @Schema(description = "Consultation fee for the appointment", example = "150000.00")
    BigDecimal consultationFee;

    @Schema(description = "Timestamp when the appointment was created", example = "2024-12-01T14:30:00")
    LocalDateTime createdAt;
}
