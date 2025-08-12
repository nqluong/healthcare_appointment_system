package project.healthcare_appointment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.healthcare_appointment.dto.request.appointment_request.*;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.appointment_response.AppointmentActionResponse;
import project.healthcare_appointment.dto.response.appointment_response.AppointmentSummaryResponse;
import project.healthcare_appointment.dto.response.appointment_response.BookAppointmentResponse;
import project.healthcare_appointment.dto.response.appointment_response.CancelAppointmentResponse;
import project.healthcare_appointment.enums.AppointmentStatus;
import project.healthcare_appointment.service.appointment_service.AppointmentService;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Appointment Management", description = "APIs for managing healthcare appointments including booking, cancellation, and retrieval")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    @Operation(
            summary = "Book a new appointment",
            description = "Books a new appointment for a patient with an available doctor slot. " +
                    "The slot must be available and not expired. Patient cannot have conflicting appointments.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Appointment successfully booked",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookAppointmentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or validation errors",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions - requires PATIENT or ADMIN role",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found or slot not available",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Slot expired, not available, or patient has conflicting appointment",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<BookAppointmentResponse> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request) {

        log.info("Received request to book appointment: {}", request);

        BookAppointmentResponse response = appointmentService.bookAppointment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/cancel")
    @Operation(
            summary = "Cancel an existing appointment",
            description = "Cancels an existing appointment and makes the slot available again. " +
                    "Only PENDING or CONFIRMED appointments can be cancelled. " +
                    "Must cancel at least 1 day before appointment.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment successfully cancelled",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CancelAppointmentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or appointment not cancellable (already cancelled/completed or too late)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions - patient can only cancel own appointments",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<CancelAppointmentResponse> cancelAppointment(
            @Valid @RequestBody CancelAppointmentRequest request) {

        log.info("Received request to cancel appointment: {}", request);

        CancelAppointmentResponse response = appointmentService.cancelAppointment(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{appointmentId}/approve")
    @Operation(
            summary = "Approve an appointment",
            description = "Doctor approves a pending appointment. Only doctors can approve their own appointments.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment successfully approved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentActionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Appointment cannot be approved (wrong status)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions - only doctor can approve their appointments",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<AppointmentActionResponse> approveAppointment(
            @Parameter(description = "UUID of the appointment", required = true)
            @PathVariable UUID appointmentId,
            @Valid @RequestBody ApproveAppointmentRequest request) {

        log.info("Received request to approve appointment: {}", appointmentId);

        AppointmentActionResponse response = appointmentService.approveAppointment(appointmentId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{appointmentId}/reject")
    @Operation(
            summary = "Reject an appointment",
            description = "Doctor rejects a pending appointment and provides a reason. " +
                    "The slot becomes available again for other patients.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment successfully rejected",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentActionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Appointment cannot be rejected (wrong status)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions - only doctor can reject their appointments",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<AppointmentActionResponse> rejectAppointment(
            @Parameter(description = "UUID of the appointment", required = true)
            @PathVariable UUID appointmentId,
            @Valid @RequestBody RejectAppointmentRequest request) {

        log.info("Received request to reject appointment: {}", appointmentId);

        AppointmentActionResponse response = appointmentService.rejectAppointment(appointmentId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
            summary = "Get patient appointments",
            description = "Retrieves a paginated list of appointments for a specific patient, " +
                    "ordered by appointment date and start time in descending order by default.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved patient appointments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions - patient can only view own appointments",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<PageResponse<AppointmentSummaryResponse>> getPatientAppointments(
            @Parameter(description = "UUID of the patient", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID patientId,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "appointmentDate")
            @RequestParam(defaultValue = "appointmentDate") String sortBy,

            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PageResponse<AppointmentSummaryResponse> appointments = appointmentService.getPatientAppointments(patientId, pageable);

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(
            summary = "Get doctor appointments",
            description = "Retrieves a paginated list of appointments for a specific doctor, " +
                    "ordered by appointment date and start time in descending order by default.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved doctor appointments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions - doctor can only view own appointments",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<PageResponse<AppointmentSummaryResponse>> getDoctorAppointments(
            @Parameter(description = "UUID of the doctor", required = true, example = "123e4567-e89b-12d3-a456-426614174001")
            @PathVariable UUID doctorId,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "appointmentDate")
            @RequestParam(defaultValue = "appointmentDate") String sortBy,

            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PageResponse<AppointmentSummaryResponse> appointments = appointmentService.getDoctorAppointments(doctorId, pageable);

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/admin/all")
    @Operation(
            summary = "Get all appointments (Admin only)",
            description = "Retrieves a paginated and filtered list of all appointments in the system. " +
                    "Supports filtering by status, date range, doctor, and patient.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all appointments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter parameters provided",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions - requires ADMIN role",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<PageResponse<AppointmentSummaryResponse>> getAllAppointments(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "appointmentDate")
            @RequestParam(defaultValue = "appointmentDate") String sortBy,

            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir,

            @Parameter(description = "Filter by appointment status", example = "PENDING")
            @RequestParam(required = false) AppointmentStatus status,

            @Parameter(description = "Filter by doctor ID", example = "123e4567-e89b-12d3-a456-426614174001")
            @RequestParam(required = false) UUID doctorId,

            @Parameter(description = "Filter by patient ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam(required = false) UUID patientId,

            @Parameter(description = "Start date for date range filter (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date for date range filter (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Filter by doctor name (partial match)", example = "John")
            @RequestParam(required = false) String doctorName,

            @Parameter(description = "Filter by patient name (partial match)", example = "Jane")
            @RequestParam(required = false) String patientName) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        AppointmentFilterRequest filterRequest = AppointmentFilterRequest.builder()
                .status(status)
                .doctorId(doctorId)
                .patientId(patientId)
                .startDate(startDate)
                .endDate(endDate)
                .doctorName(doctorName)
                .patientName(patientName)
                .build();

        PageResponse<AppointmentSummaryResponse> appointments = appointmentService.getAllAppointments(pageable, filterRequest);

        return ResponseEntity.ok(appointments);
    }
}
