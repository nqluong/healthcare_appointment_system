package project.healthcare_appointment.service.doctor_service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.healthcare_appointment.dto.request.doctor_request.CreateDoctorRequest;
import project.healthcare_appointment.dto.request.doctor_request.UpdateDoctorRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.doctor_response.DoctorResponse;

import java.util.UUID;

public interface DoctorService {

    /**
     * Creates a new doctor based on the provided request.
     *
     * @param request the request containing doctor details
     * @return the created doctor's response
     */
    DoctorResponse createDoctor(CreateDoctorRequest request);

    DoctorResponse getDoctorById(UUID id);

    PageResponse<DoctorResponse> getAllDoctors(String name, UUID specialtyId, Boolean approved, Pageable pageable);

    DoctorResponse updateDoctor(UUID id, UpdateDoctorRequest requestDto);

    void deleteDoctor(UUID id);

    PageResponse<DoctorResponse> getDoctorsBySpecialty(UUID specialtyId, Pageable pageable);

    PageResponse<DoctorResponse> getDoctorsByApprovalStatus(Boolean approved, Pageable pageable);

}
