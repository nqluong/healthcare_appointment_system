package project.healthcare_appointment.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.available_response.AvailableSlotResponse;
import project.healthcare_appointment.dto.response.doctor_response.DoctorResponse;
import project.healthcare_appointment.model.Doctor;
import project.healthcare_appointment.model.DoctorAvailableSlot;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AvailableSlotMapper {
    AvailableSlotResponse toResponse(DoctorAvailableSlot slot);

    List<AvailableSlotResponse> toResponseList(List<DoctorAvailableSlot> slots);

    default PageResponse<AvailableSlotResponse> toResponsePage(Page<DoctorAvailableSlot> page) {
        List<AvailableSlotResponse> content = page.getContent().stream()
                .map(this::toResponse)
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
