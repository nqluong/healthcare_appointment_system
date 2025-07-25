package project.healthcare_appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.healthcare_appointment.model.Doctor;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    Optional<Doctor> findByUserId(UUID userId);

    @Query("SELECT d FROM Doctor d WHERE d.isApproved = :approved")
    Page<Doctor> findByIsApproved(@Param("approved") Boolean approved, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.specialty.id = :specialtyId")
    Page<Doctor> findBySpecialtyId(@Param("specialtyId") UUID specialtyId, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE " +
            "(:name IS NULL OR LOWER(d.user.userProfile.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(d.user.userProfile.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:specialtyId IS NULL OR d.specialty.id = :specialtyId) AND " +
            "(:approved IS NULL OR d.isApproved = :approved)")
    Page<Doctor> findDoctorsWithFilters(@Param("name") String name,
                                        @Param("specialtyId") UUID specialtyId,
                                        @Param("approved") Boolean approved,
                                        Pageable pageable);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumberAndIdNot(String licenseNumber, UUID id);
}
