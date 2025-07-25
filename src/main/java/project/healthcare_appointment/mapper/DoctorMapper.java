package project.healthcare_appointment.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import project.healthcare_appointment.dto.request.doctor_request.CreateDoctorRequest;
import project.healthcare_appointment.dto.request.doctor_request.UpdateDoctorRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.doctor_response.DoctorResponse;
import project.healthcare_appointment.dto.response.specialty_response.SpecialtyBasicResponse;
import project.healthcare_appointment.dto.response.user_profile_response.UserBasicResponse;
import project.healthcare_appointment.model.Doctor;
import project.healthcare_appointment.model.Specialty;
import project.healthcare_appointment.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DoctorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "availableSlots", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "specialty", source = "specialty")
    @Mapping(target = "isApproved", constant = "true")
    Doctor toEntity(CreateDoctorRequest dto, User user, Specialty specialty);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "availableSlots", ignore = true)
    @Mapping(target = "specialty", source = "specialty")
    void updateEntity(@MappingTarget Doctor entity, UpdateDoctorRequest dto, Specialty specialty);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "specialty", source = "specialty")
    DoctorResponse toResponseDto(Doctor entity);

    List<DoctorResponse> toResponseList(List<Doctor> entities);

    default PageResponse<DoctorResponse> toResponsePage(Page<Doctor> page) {
        List<DoctorResponse> content = page.getContent().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserBasicResponse toUserBasicDto(User user);

    SpecialtyBasicResponse toSpecialtyBasicDto(Specialty specialty);
}
