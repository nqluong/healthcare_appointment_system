package project.healthcare_appointment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.healthcare_appointment.dto.request.doctor_request.CreateDoctorRequest;
import project.healthcare_appointment.dto.request.doctor_request.UpdateDoctorRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.doctor_response.DoctorResponse;
import project.healthcare_appointment.service.doctor_service.DoctorService;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "Admin operations for managing doctors")
@SecurityRequirement(name = "bearerAuth")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @Operation(summary = "Create a new doctor", description = "Creates a new doctor account with the provided information",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "409", description = "Doctor with license number already exists")
    })
    public ResponseEntity<DoctorResponse> createDoctor(
            @Valid @RequestBody CreateDoctorRequest requestDto) {
        DoctorResponse response = doctorService.createDoctor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID", description = "Retrieves detailed information about a specific doctor",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<DoctorResponse> getDoctorById(
            @Parameter(description = "Doctor ID") @PathVariable UUID id) {
        DoctorResponse response = doctorService.getDoctorById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all doctors with filtering", description = "Retrieves a paginated list of doctors with optional filtering",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<PageResponse<DoctorResponse>> getAllDoctors(
            @Parameter(description = "Filter by doctor name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by specialty ID") @RequestParam(required = false) UUID specialtyId,
            @Parameter(description = "Filter by approval status") @RequestParam(required = false) Boolean approved,
            @Parameter(description = "Page number (0-based index)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<DoctorResponse> response = doctorService.getAllDoctors(name, specialtyId, approved, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update doctor information", description = "Updates an existing doctor's information",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "409", description = "License number already exists")
    })
    public ResponseEntity<DoctorResponse> updateDoctor(
            @Parameter(description = "Doctor ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateDoctorRequest requestDto) {
        DoctorResponse response = doctorService.updateDoctor(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete doctor", description = "Deletes a doctor account",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<Void> deleteDoctor(
            @Parameter(description = "Doctor ID") @PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/approval-status/{approved}")
    @Operation(summary = "Get doctors by approval status", description = "Retrieves doctors filtered by approval status",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<PageResponse<DoctorResponse>> getDoctorsByApprovalStatus(
            @Parameter(description = "Approval status") @PathVariable Boolean approved,
            @Parameter(description = "Page number (0-based index)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<DoctorResponse> response = doctorService.getDoctorsByApprovalStatus(approved, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/specialty/{specialtyId}")
    @Operation(summary = "Get doctors by specialty", description = "Retrieves doctors filtered by specialty",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<PageResponse<DoctorResponse>> getDoctorsBySpecialty(
            @Parameter(description = "Specialty ID") @PathVariable UUID specialtyId,
            @Parameter(description = "Page number (0-based index)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<DoctorResponse> response = doctorService.getDoctorsBySpecialty(specialtyId, pageable);
        return ResponseEntity.ok(response);
    }
}
