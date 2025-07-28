package project.healthcare_appointment.service.appointment_service;


import org.springframework.data.domain.Pageable;
import project.healthcare_appointment.dto.request.appointment_request.BookAppointmentRequest;
import project.healthcare_appointment.dto.request.appointment_request.CancelAppointmentRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.appointment_response.AppointmentSummaryResponse;
import project.healthcare_appointment.dto.response.appointment_response.BookAppointmentResponse;
import project.healthcare_appointment.dto.response.appointment_response.CancelAppointmentResponse;

import java.util.UUID;

public interface AppointmentService {

    BookAppointmentResponse bookAppointment(BookAppointmentRequest request);

    CancelAppointmentResponse cancelAppointment(CancelAppointmentRequest request);

    PageResponse<AppointmentSummaryResponse> getPatientAppointments(UUID patientId, Pageable pageable);

    public PageResponse<AppointmentSummaryResponse> getDoctorAppointments(UUID doctorId, Pageable pageable);
}
