package project.healthcare_appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.healthcare_appointment.model.Specialty;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {

    Optional<Specialty> findByName(String name);

    @Query("SELECT s FROM Specialty s WHERE s.isActive = :active")
    Page<Specialty> findByIsActive(@Param("active") Boolean active, Pageable pageable);

    @Query("SELECT s FROM Specialty s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) AND " +
            "(:active IS NULL OR s.isActive = :active)")
    Page<Specialty> findByNameContainingIgnoreCaseAndActive(
            @Param("name") String name,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query("SELECT s FROM Specialty s WHERE s.isActive = :active")
    Page<Specialty> findByActive(@Param("active") Boolean active, Pageable pageable);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
