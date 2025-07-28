package project.healthcare_appointment.service.schedule_service;

import org.springframework.data.domain.Pageable;
import project.healthcare_appointment.dto.request.schedule_request.AddAvailableSlotRequest;
import project.healthcare_appointment.dto.request.schedule_request.CreateDoctorScheduleRequest;
import project.healthcare_appointment.dto.request.schedule_request.GenerateSlotsRequest;
import project.healthcare_appointment.dto.request.schedule_request.UpdateDoctorScheduleRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.available_response.AvailableSlotResponse;
import project.healthcare_appointment.dto.response.available_response.DoctorScheduleResponse;
import project.healthcare_appointment.dto.response.available_response.SlotGenerationResult;
import project.healthcare_appointment.dto.response.specialty_response.MultipleSlotCreationResult;

import java.util.List;
import java.util.UUID;

public interface DoctorScheduleService {
    MultipleSlotCreationResult addAvailableSlot(UUID doctorId, AddAvailableSlotRequest request);

    DoctorScheduleResponse createDoctorSchedule(UUID doctorId, CreateDoctorScheduleRequest request);

    DoctorScheduleResponse updateDoctorSchedule(UUID doctorId, UUID scheduleId, UpdateDoctorScheduleRequest request);

    void deleteDoctorSchedule(UUID doctorId, UUID scheduleId);

    List<DoctorScheduleResponse> getDoctorSchedules(UUID doctorId);

    void deleteAvailableSlot(UUID doctorId, UUID slotId);

    PageResponse<AvailableSlotResponse> getAvailableSlots(UUID doctorId, Pageable pageable);

    SlotGenerationResult generateSlotsFromSchedule(UUID doctorId, GenerateSlotsRequest request);
}
