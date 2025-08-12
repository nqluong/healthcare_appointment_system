package project.healthcare_appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.healthcare_appointment.enums.AppointmentStatus;
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

    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    /**
     * Find appointments within date range
     */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate")
    Page<Appointment> findByAppointmentDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    // For simple filters without name search
    @Query("""
                SELECT a FROM Appointment a
                WHERE (:status IS NULL OR a.status = :status)
                  AND (:doctorId IS NULL OR a.doctor.id = :doctorId)
                  AND (:patientId IS NULL OR a.patient.id = :patientId)
                  AND (CAST(:startDate AS date) IS NULL OR a.appointmentDate >= CAST(:startDate AS date))
                  AND (CAST(:endDate AS date) IS NULL OR a.appointmentDate <= CAST(:endDate AS date))
                ORDER BY a.appointmentDate DESC, a.startTime DESC
            """)
    Page<Appointment> findAppointmentsWithBasicFilters(
            @Param("status") AppointmentStatus status,
            @Param("doctorId") UUID doctorId,
            @Param("patientId") UUID patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // For complex filters with name search
    @Query("SELECT a FROM Appointment a " +
            "WHERE (:status IS NULL OR a.status = :status) " +
            "AND (:doctorId IS NULL OR a.doctor.id = :doctorId) " +
            "AND (:patientId IS NULL OR a.patient.id = :patientId) " +
            "AND (CAST(:startDate AS date) IS NULL OR a.appointmentDate >= CAST(:startDate AS date)) " +
            "AND (CAST(:endDate AS date) IS NULL OR a.appointmentDate <= CAST(:endDate AS date)) " +
            "AND (:doctorName IS NULL OR :doctorName = '' OR " +
            "     LOWER(a.doctor.user.userProfile.firstName) LIKE LOWER(CONCAT('%', :doctorName, '%')) OR " +
            "     LOWER(a.doctor.user.userProfile.lastName) LIKE LOWER(CONCAT('%', :doctorName, '%'))) " +
            "AND (:patientName IS NULL OR :patientName = '' OR " +
            "     LOWER(a.patient.user.userProfile.firstName) LIKE LOWER(CONCAT('%', :patientName, '%')) OR " +
            "     LOWER(a.patient.user.userProfile.lastName) LIKE LOWER(CONCAT('%', :patientName, '%'))) " +
            "ORDER BY a.appointmentDate DESC, a.startTime DESC")
    Page<Appointment> findAppointmentsWithAllFilters(
            @Param("status") AppointmentStatus status,
            @Param("doctorId") UUID doctorId,
            @Param("patientId") UUID patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("doctorName") String doctorName,
            @Param("patientName") String patientName,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = :status")
    long countByDoctorIdAndStatus(@Param("doctorId") UUID doctorId, @Param("status") AppointmentStatus status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId AND a.status = :status")
    long countByPatientIdAndStatus(@Param("patientId") UUID patientId, @Param("status") AppointmentStatus status);
}
