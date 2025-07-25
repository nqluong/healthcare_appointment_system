package project.healthcare_appointment.service.specialty_service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.healthcare_appointment.dto.request.specialty_request.CreateSpecialtyRequest;
import project.healthcare_appointment.dto.request.specialty_request.UpdateSpecialtyRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.specialty_response.SpecialtyResponse;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.mapper.SpecialtyMapper;
import project.healthcare_appointment.model.Specialty;
import project.healthcare_appointment.repository.SpecialtyRepository;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class SpecialtyServiceImpl implements SpecialtyService {

    SpecialtyRepository specialtyRepository;
    SpecialtyMapper specialtyMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SpecialtyResponse createSpecialty(CreateSpecialtyRequest request) {
        log.info("Creating specialty with name: {}", request.getName());

        // Validate name uniqueness
        if (specialtyRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.SPECIALTY_NAME_DUPLICATED, "Specialty with name '" + request.getName() + "' already exists");
        }

        Specialty specialty = specialtyMapper.toEntity(request);
        Specialty savedSpecialty = specialtyRepository.save(specialty);

        log.info("Specialty created successfully with ID: {}", savedSpecialty.getId());
        return specialtyMapper.toResponseDto(savedSpecialty);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public SpecialtyResponse getSpecialtyById(UUID id) {
        log.info("Fetching specialty with ID: {}", id);

        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALIST_NOT_FOUND, "Specialty not found with ID:" + id));

        return specialtyMapper.toResponseDto(specialty);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public PageResponse<SpecialtyResponse> getAllSpecialties(String name, Boolean active, Pageable pageable) {
        log.info("Fetching specialties with filters - name: {}, active: {}", name, active);
        Page<Specialty> specialties;

        if (name != null && !name.trim().isEmpty()) {
            specialties = specialtyRepository.findByNameContainingIgnoreCaseAndActive(
                    name.trim(), active, pageable);
        } else if (active != null) {
            specialties = specialtyRepository.findByActive(active, pageable);
        } else {
            specialties = specialtyRepository.findAll(pageable);
        }

        return specialtyMapper.toResponsePage(specialties);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SpecialtyResponse updateSpecialty(UUID id, UpdateSpecialtyRequest request) {
        log.info("Updating specialty with ID: {}", id);

        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALIST_NOT_FOUND, "Specialty not found with ID: " + id));

        // Validate name uniqueness if being updated
        if (request.getName() != null &&
                specialtyRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new AppException(ErrorCode.SPECIALTY_NAME_DUPLICATED, "Specialty with name '" + request.getName() + "' already exists");
        }

        specialtyMapper.updateEntity(specialty, request);
        Specialty updatedSpecialty = specialtyRepository.save(specialty);

        log.info("Specialty updated successfully with ID: {}", updatedSpecialty.getId());
        return specialtyMapper.toResponseDto(updatedSpecialty);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteSpecialty(UUID id) {
        log.info("Deleting specialty with ID: {}", id);

        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALIST_NOT_FOUND, "Specialty not found with ID: " + id));

        // Check if specialty has associated doctors
        if (!specialty.getDoctors().isEmpty()) {
            throw new IllegalStateException("Cannot delete specialty with associated doctors. Please reassign doctors first.");
        }

        specialtyRepository.delete(specialty);
        log.info("Specialty deleted successfully with ID: {}", id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public PageResponse<SpecialtyResponse> getActiveSpecialties(Pageable pageable) {
        log.info("Fetching active specialties");

        Page<Specialty> specialties = specialtyRepository.findByIsActive(true, pageable);
        return specialtyMapper.toResponsePage(specialties);
    }
}
