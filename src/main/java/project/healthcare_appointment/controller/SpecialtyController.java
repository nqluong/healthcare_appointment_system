package project.healthcare_appointment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.healthcare_appointment.dto.request.specialty_request.CreateSpecialtyRequest;
import project.healthcare_appointment.dto.request.specialty_request.UpdateSpecialtyRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.specialty_response.SpecialtyResponse;
import project.healthcare_appointment.service.specialty_service.SpecialtyService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin/specialties")
@RequiredArgsConstructor
@Tag(name = "Specialty Management", description = "Admin operations for managing medical specialties")
@SecurityRequirement(name = "bearerAuth")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    @Operation(summary = "Create a new specialty", description = "Creates a new medical specialty",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Specialty created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "409", description = "Specialty with name already exists")
    })
    public ResponseEntity<SpecialtyResponse> createSpecialty(
            @Valid @RequestBody CreateSpecialtyRequest requestDto) {
        SpecialtyResponse response = specialtyService.createSpecialty(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specialty by ID", description = "Retrieves detailed information about a specific specialty",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Specialty retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Specialty not found")
    })
    public ResponseEntity<SpecialtyResponse> getSpecialtyById(
            @Parameter(description = "Specialty ID") @PathVariable UUID id) {
        SpecialtyResponse response = specialtyService.getSpecialtyById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all specialties with filtering", description = "Retrieves a paginated list of specialties with optional filtering",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Specialties retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<PageResponse<SpecialtyResponse>> getAllSpecialties(
            @Parameter(description = "Filter by specialty name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Page number (0-based index)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {


        Pageable pageable = PageRequest.of(page, size);
        PageResponse<SpecialtyResponse> response = specialtyService.getAllSpecialties(name, active, pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update specialty information", description = "Updates an existing specialty's information",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Specialty updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Specialty not found"),
            @ApiResponse(responseCode = "409", description = "Specialty name already exists")
    })
    public ResponseEntity<SpecialtyResponse> updateSpecialty(
            @Parameter(description = "Specialty ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateSpecialtyRequest requestDto) {
        SpecialtyResponse response = specialtyService.updateSpecialty(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete specialty", description = "Deletes a specialty (only if no doctors are associated)",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Specialty deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot delete specialty with associated doctors"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Specialty not found")
    })
    public ResponseEntity<Void> deleteSpecialty(
            @Parameter(description = "Specialty ID") @PathVariable UUID id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    @Operation(summary = "Get active specialties", description = "Retrieves only active specialties",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active specialties retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<PageResponse<SpecialtyResponse>> getActiveSpecialties(
            @Parameter(description = "Page number (0-based index)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<SpecialtyResponse> response = specialtyService.getActiveSpecialties(pageable);
        return ResponseEntity.ok(response);
    }
}
