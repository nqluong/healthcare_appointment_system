package project.healthcare_appointment.dto.request.appointment_request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import project.healthcare_appointment.enums.AppointmentStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Filter criteria for admin appointment search")
public class AppointmentFilterRequest {
    @Schema(description = "Filter by appointment status", example = "PENDING")
    AppointmentStatus status;

    @Schema(description = "Filter by doctor ID", example = "123e4567-e89b-12d3-a456-426614174001")
    UUID doctorId;

    @Schema(description = "Filter by patient ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID patientId;

    @Schema(description = "Start date for date range filter", example = "2024-01-01")
    LocalDate startDate;

    @Schema(description = "End date for date range filter", example = "2024-12-31")
    LocalDate endDate;

    @Schema(description = "Filter by doctor name (partial match)", example = "Luong")
    String doctorName;

    @Schema(description = "Filter by patient name (partial match)", example = "Nguyen")
    String patientName;
}
