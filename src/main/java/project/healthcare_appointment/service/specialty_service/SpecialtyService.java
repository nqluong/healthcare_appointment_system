package project.healthcare_appointment.service.specialty_service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.healthcare_appointment.dto.request.specialty_request.CreateSpecialtyRequest;
import project.healthcare_appointment.dto.request.specialty_request.UpdateSpecialtyRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.specialty_response.SpecialtyResponse;

import java.util.UUID;

public interface SpecialtyService {

    SpecialtyResponse createSpecialty(CreateSpecialtyRequest request);

    SpecialtyResponse getSpecialtyById(UUID id);

    PageResponse<SpecialtyResponse> getAllSpecialties(String name, Boolean active, Pageable pageable);

    SpecialtyResponse updateSpecialty(UUID id, UpdateSpecialtyRequest request);

    void deleteSpecialty(UUID id);

    PageResponse<SpecialtyResponse> getActiveSpecialties(Pageable pageable);
}
