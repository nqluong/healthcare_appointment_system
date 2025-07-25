package project.healthcare_appointment.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import project.healthcare_appointment.dto.request.specialty_request.CreateSpecialtyRequest;
import project.healthcare_appointment.dto.request.specialty_request.UpdateSpecialtyRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.specialty_response.SpecialtyResponse;
import project.healthcare_appointment.model.Specialty;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SpecialtyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Specialty toEntity(CreateSpecialtyRequest dto);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    void updateEntity(@MappingTarget Specialty entity, UpdateSpecialtyRequest dto);

    @Mapping(target = "doctorCount", expression = "java(entity.getDoctors() != null ? (long) entity.getDoctors().size() : 0L)")
    SpecialtyResponse toResponseDto(Specialty entity);

    List<SpecialtyResponse> toResponseList(List<Specialty> entities);

    default PageResponse<SpecialtyResponse> toResponsePage(Page<Specialty> page) {
        List<SpecialtyResponse> content = page.getContent().stream()
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
}
