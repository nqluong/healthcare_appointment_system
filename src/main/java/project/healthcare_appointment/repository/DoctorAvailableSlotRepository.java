package project.healthcare_appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.healthcare_appointment.model.DoctorAvailableSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorAvailableSlotRepository extends JpaRepository<DoctorAvailableSlot, UUID> {
    Page<DoctorAvailableSlot> findByDoctorIdOrderBySlotDateAscStartTimeAsc(UUID doctorId, Pageable pageable);

    List<DoctorAvailableSlot> findByDoctorIdAndSlotDateBetween(UUID doctorId, LocalDate startDate, LocalDate endDate);

    boolean existsByDoctorIdAndSlotDateAndStartTime(UUID doctorId, LocalDate slotDate, LocalTime startTime);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentDate = :slotDate AND a.startTime = :startTime " +
            "AND a.status  != 'CANCELLED'")
    boolean existsBookedAppointment(@Param("doctorId") UUID doctorId,
                                    @Param("slotDate") LocalDate slotDate,
                                    @Param("startTime") LocalTime startTime);

    @Modifying
    @Query("DELETE FROM DoctorAvailableSlot s WHERE s.doctor.id = :doctorId AND s.slotDate = :slotDate AND s.startTime = :startTime")
    void deleteByDoctorIdAndSlotDateAndStartTime(@Param("doctorId") UUID doctorId,
                                                 @Param("slotDate") LocalDate slotDate,
                                                 @Param("startTime") LocalTime startTime);
}
