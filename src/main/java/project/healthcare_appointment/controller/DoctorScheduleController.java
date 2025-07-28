package project.healthcare_appointment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.healthcare_appointment.dto.request.schedule_request.AddAvailableSlotRequest;
import project.healthcare_appointment.dto.request.schedule_request.CreateDoctorScheduleRequest;
import project.healthcare_appointment.dto.request.schedule_request.GenerateSlotsRequest;
import project.healthcare_appointment.dto.request.schedule_request.UpdateDoctorScheduleRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.available_response.AvailableSlotResponse;
import project.healthcare_appointment.dto.response.available_response.DoctorScheduleResponse;
import project.healthcare_appointment.dto.response.available_response.SlotGenerationResult;
import project.healthcare_appointment.dto.response.specialty_response.MultipleSlotCreationResult;
import project.healthcare_appointment.service.schedule_service.DoctorScheduleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors/{doctorId}/schedule")
@RequiredArgsConstructor
@Tag(name = "Doctor Schedule Management", description = "APIs for managing doctor schedules and available slots")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class DoctorScheduleController {
    private final DoctorScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "Create doctor schedule", description = "Create a new periodic schedule for a doctor",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Schedule created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "409", description = "Schedule already exists for this day")
    })
    public ResponseEntity<DoctorScheduleResponse> createDoctorSchedule(
            @Parameter(description = "Doctor ID") @PathVariable UUID doctorId,
            @Valid @RequestBody CreateDoctorScheduleRequest request) {

        DoctorScheduleResponse response = scheduleService.createDoctorSchedule(doctorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get doctor schedules", description = "Get all periodic schedules for a doctor",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Schedules retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<List<DoctorScheduleResponse>> getDoctorSchedules(
            @Parameter(description = "Doctor ID") @PathVariable UUID doctorId) {

        List<DoctorScheduleResponse> response = scheduleService.getDoctorSchedules(doctorId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{scheduleId}")
    @Operation(summary = "Update doctor schedule", description = "Update an existing periodic schedule",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    public ResponseEntity<DoctorScheduleResponse> updateDoctorSchedule(
            @Parameter(description = "Doctor ID") @PathVariable UUID doctorId,
            @Parameter(description = "Schedule ID") @PathVariable UUID scheduleId,
            @Valid @RequestBody UpdateDoctorScheduleRequest request) {

        DoctorScheduleResponse response = scheduleService.updateDoctorSchedule(doctorId, scheduleId, request);
        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "Delete doctor schedule", description = "Delete a periodic schedule",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Schedule deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    public ResponseEntity<Void> deleteDoctorSchedule(
            @Parameter(description = "Doctor ID") @PathVariable UUID doctorId,
            @Parameter(description = "Schedule ID") @PathVariable UUID scheduleId) {

        scheduleService.deleteDoctorSchedule(doctorId, scheduleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/slots")
    @Operation(summary = "Add available slot", description = "Doctor adds a specific available slot",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Slot created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "409", description = "Slot already exists")
    })
    public ResponseEntity<MultipleSlotCreationResult> addAvailableSlot(
            @Parameter(description = "Doctor ID") @PathVariable UUID doctorId,
            @Valid @RequestBody AddAvailableSlotRequest request) {

        MultipleSlotCreationResult response = scheduleService.addAvailableSlot(doctorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/slots/{slotId}")
    @Operation(summary = "Delete available slot", description = "Doctor deletes a specific available slot",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Slot deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Slot is booked and cannot be deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Slot not found")
    })
    public ResponseEntity<Void> deleteAvailableSlot(
            @Parameter(description = "Doctor ID") @PathVariable UUID doctorId,
            @Parameter(description = "Slot ID") @PathVariable UUID slotId) {

        scheduleService.deleteAvailableSlot(doctorId, slotId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/slots")
    @Operation(summary = "Get available slots", description = "Get paginated list of doctor's available slots",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Slots retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<PageResponse<AvailableSlotResponse>> getAvailableSlots(
            @Parameter(description = "Doctor ID") @PathVariable UUID doctorId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "slotDate") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        PageResponse<AvailableSlotResponse> response = scheduleService.getAvailableSlots(doctorId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-slots")
    @Operation(summary = "Generate slots from schedule",
            description = "Automatically generate available slots from doctor's periodic schedule (creates default schedule if none exists)",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Slots generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or no active schedules"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<SlotGenerationResult> generateSlotsFromSchedule(
            @Parameter(description = "Doctor ID") @PathVariable UUID doctorId,
            @Valid @RequestBody GenerateSlotsRequest request) {

        SlotGenerationResult result = scheduleService.generateSlotsFromSchedule(doctorId, request);
        return ResponseEntity.ok(result);
    }
}
