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
@Schema(description = "Summary information of an appointment")
public class AppointmentSummaryResponse {

    @Schema(description = "Unique identifier of the appointment", example = "123e4567-e89b-12d3-a456-426614174002")
    UUID appointmentId;

    @Schema(description = "Unique identifier of the doctor", example = "123e4567-e89b-12d3-a456-426614174003")
    UUID doctorId;

    @Schema(description = "Full name of the doctor", example = "Nguyen Luong")
    String doctorName;

    @Schema(description = "Medical specialty of the doctor", example = "Ha Noi")
    String doctorSpecialty;

    @Schema(description = "Unique identifier of the patient", example = "123e4567-e89b-12d3-a456-426614174001")
    UUID patientId;

    @Schema(description = "Full name of the patient", example = "Nguyen Van A")
    String patientName;

    @Schema(description = "Date of the appointment", example = "2024-12-15")
    LocalDate appointmentDate;

    @Schema(description = "Start time of the appointment", example = "10:00:00")
    LocalTime startTime;

    @Schema(description = "End time of the appointment", example = "10:30:00")
    LocalTime endTime;

    @Schema(description = "Current status of the appointment", example = "CONFIRMED")
    AppointmentStatus status;

    @Schema(description = "Reason for the appointment", example = "Regular health checkup")
    String reason;

    @Schema(description = "Consultation fee for the appointment", example = "200000.00")
    BigDecimal consultationFee;

    @Schema(description = "Timestamp when the appointment was created", example = "2024-12-01T14:30:00")
    LocalDateTime createdAt;

    @Schema(description = "Timestamp when the appointment was last updated", example = "2024-12-01T14:30:00")
    LocalDateTime updatedAt;
}
