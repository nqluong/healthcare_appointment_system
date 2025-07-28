package project.healthcare_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.healthcare_appointment.model.DoctorSchedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {
    List<DoctorSchedule> findByDoctorIdAndIsActiveTrue(UUID doctorId);

    List<DoctorSchedule> findByDoctorIdOrderByDayOfWeekAsc(UUID doctorId);

    Optional<DoctorSchedule> findByDoctorIdAndDayOfWeek(UUID doctorId, Integer dayOfWeek);

    boolean existsByDoctorIdAndDayOfWeek(UUID doctorId, Integer dayOfWeek);


}
