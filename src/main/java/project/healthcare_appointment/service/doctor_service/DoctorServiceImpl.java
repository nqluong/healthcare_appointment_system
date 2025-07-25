package project.healthcare_appointment.service.doctor_service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.healthcare_appointment.dto.request.doctor_request.CreateDoctorRequest;
import project.healthcare_appointment.dto.request.doctor_request.UpdateDoctorRequest;
import project.healthcare_appointment.dto.response.doctor_response.DoctorResponse;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.mapper.DoctorMapper;
import project.healthcare_appointment.model.Doctor;
import project.healthcare_appointment.model.Specialty;
import project.healthcare_appointment.model.User;
import project.healthcare_appointment.repository.DoctorRepository;
import project.healthcare_appointment.repository.SpecialtyRepository;
import project.healthcare_appointment.repository.UserRepository;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class DoctorServiceImpl implements DoctorService {
    DoctorRepository doctorRepository;
    UserRepository userRepository;
    SpecialtyRepository specialtyRepository;
    DoctorMapper doctorMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DoctorResponse createDoctor(CreateDoctorRequest requestDto) {
        log.info("Creating doctor with license number: {}", requestDto.getLicenseNumber());

        // Validate license number uniqueness
        if (doctorRepository.existsByLicenseNumber(requestDto.getLicenseNumber())) {
            throw new AppException(ErrorCode.DOCTOR_LICENSE_DUPLICATED, "Doctor with license number '" + requestDto.getLicenseNumber() + "' already exists");
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found with ID: " + requestDto.getUserId()));

        // Fetch specialty if provided
        Specialty specialty = null;
        if (requestDto.getSpecialtyId() != null) {
            specialty = specialtyRepository.findById(requestDto.getSpecialtyId())
                    .orElseThrow(() -> new AppException(ErrorCode.SPECIALIST_NOT_FOUND,"Specialty not found with ID: " + requestDto.getSpecialtyId()));
        }

        Doctor doctor = doctorMapper.toEntity(requestDto, user, specialty);
        Doctor savedDoctor = doctorRepository.save(doctor);

        log.info("Doctor created successfully with ID: {}", savedDoctor.getId());
        return doctorMapper.toResponseDto(savedDoctor);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public DoctorResponse getDoctorById(UUID id) {
        log.info("Fetching doctor with ID: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found with ID:" + id));

        return doctorMapper.toResponseDto(doctor);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public Page<DoctorResponse> getAllDoctors(String name, UUID specialtyId, Boolean approved, Pageable pageable) {

        log.info("Fetching doctors with filters - name: {}, specialtyId: {}, approved: {}", name, specialtyId, approved);

        Page<Doctor> doctors = doctorRepository.findDoctorsWithFilters(name, specialtyId, approved, pageable);
        return doctorMapper.toResponseDtoPage(doctors);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DoctorResponse updateDoctor(UUID id, UpdateDoctorRequest requestDto) {
        log.info("Updating doctor with ID: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found with ID: " + id));

        // Validate license number uniqueness if being updated
        if (requestDto.getLicenseNumber() != null &&
                doctorRepository.existsByLicenseNumberAndIdNot(requestDto.getLicenseNumber(), id)) {
            throw new AppException(ErrorCode.DOCTOR_LICENSE_DUPLICATED,"Doctor with license number '" + requestDto.getLicenseNumber() + "' already exists");
        }

        // Fetch specialty if being updated
        Specialty specialty = null;
        if (requestDto.getSpecialtyId() != null) {
            specialty = specialtyRepository.findById(requestDto.getSpecialtyId())
                    .orElseThrow(() -> new AppException(ErrorCode.SPECIALIST_NOT_FOUND,"Specialty not found with ID: " + requestDto.getSpecialtyId()));
        }

        doctorMapper.updateEntity(doctor, requestDto, specialty);
        Doctor updatedDoctor = doctorRepository.save(doctor);

        log.info("Doctor updated successfully with ID: {}", updatedDoctor.getId());
        return doctorMapper.toResponseDto(updatedDoctor);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteDoctor(UUID id) {
        log.info("Deleting doctor with ID: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found with ID: " + id));

        doctorRepository.delete(doctor);
        log.info("Doctor deleted successfully with ID: {}", id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<DoctorResponse> getDoctorsByApprovalStatus(Boolean approved, Pageable pageable) {
        log.info("Fetching doctors by approval status: {}", approved);

        Page<Doctor> doctors = doctorRepository.findByIsApproved(approved, pageable);
        return doctorMapper.toResponseDtoPage(doctors);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('PATIENT')")
    public Page<DoctorResponse> getDoctorsBySpecialty(UUID specialtyId, Pageable pageable) {
        log.info("Fetching doctors by specialty ID: {}", specialtyId);

        Page<Doctor> doctors = doctorRepository.findBySpecialtyId(specialtyId, pageable);
        return doctorMapper.toResponseDtoPage(doctors);
    }

}
