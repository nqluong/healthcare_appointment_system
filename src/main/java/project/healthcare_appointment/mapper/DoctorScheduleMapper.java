package project.healthcare_appointment.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.available_response.DoctorScheduleResponse;
import project.healthcare_appointment.dto.response.doctor_response.DoctorResponse;
import project.healthcare_appointment.model.Doctor;
import project.healthcare_appointment.model.DoctorSchedule;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DoctorScheduleMapper {
    DoctorScheduleResponse toResponse(DoctorSchedule schedule);
    List<DoctorScheduleResponse> toResponseList(List<DoctorSchedule> schedules);


}
