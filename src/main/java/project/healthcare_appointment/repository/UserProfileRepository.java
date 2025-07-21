package project.healthcare_appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.healthcare_appointment.model.UserProfile;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user WHERE up.user.id = :userId")
    Optional<UserProfile> findByUserIdWithUser(@Param("userId") UUID userId);

    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user")
    Page<UserProfile> findAllWithUser(Pageable pageable);

    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user u WHERE " +
            "LOWER(up.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(up.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<UserProfile> searchProfiles(@Param("keyword") String keyword, Pageable pageable);
}
