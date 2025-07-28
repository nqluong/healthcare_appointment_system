package project.healthcare_appointment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.appointment_response.AppointmentSummaryResponse;
import project.healthcare_appointment.dto.response.appointment_response.BookAppointmentResponse;
import project.healthcare_appointment.dto.response.doctor_response.DoctorResponse;
import project.healthcare_appointment.model.Appointment;
import project.healthcare_appointment.model.Doctor;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {
    @Mapping(target = "doctorName", source = "doctor.user.userProfile.firstName")
    @Mapping(target = "doctorSpecialty", source = "doctor.specialty.name")
    @Mapping(target = "patientName", source = "patient.user.userProfile.firstName")
    @Mapping(target = "appointmentId", source = "id")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    AppointmentSummaryResponse toSummaryDto(Appointment appointment);

    @Mapping(target = "appointmentId", source = "id")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", source = "doctor.user.userProfile.firstName")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientName", source = "patient.user.userProfile.firstName")
    BookAppointmentResponse toBookResponse(Appointment appointment);

    List<AppointmentSummaryResponse> toSummaryDtoList(List<Appointment> appointments);

    default PageResponse<AppointmentSummaryResponse> toResponsePage(Page<Appointment> page) {
        List<AppointmentSummaryResponse> content = page.getContent().stream()
                .map(this::toSummaryDto)
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
