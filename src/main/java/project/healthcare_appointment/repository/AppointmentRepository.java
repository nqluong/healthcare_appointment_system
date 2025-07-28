package project.healthcare_appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.healthcare_appointment.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId " +
            "AND a.appointmentDate = :date AND a.startTime < :endTime AND a.endTime > :startTime " +
            "AND a.status NOT IN ('CANCELLED', 'REJECTED')")
    List<Appointment> findConflictingAppointments(@Param("patientId") UUID patientId,
                                                  @Param("date") LocalDate date,
                                                  @Param("startTime") LocalTime startTime,
                                                  @Param("endTime") LocalTime endTime);

    @Query("SELECT a FROM Appointment a WHERE a.id = :appointmentId AND a.patient.id = :patientId")
    Optional<Appointment> findByIdAndPatientId(@Param("appointmentId") UUID appointmentId,
                                               @Param("patientId") UUID patientId);

    Page<Appointment> findByPatientIdOrderByAppointmentDateDescStartTimeDesc(UUID patientId, Pageable pageable);

    Page<Appointment> findByDoctorIdOrderByAppointmentDateDescStartTimeDesc(UUID doctorId, Pageable pageable);
}
