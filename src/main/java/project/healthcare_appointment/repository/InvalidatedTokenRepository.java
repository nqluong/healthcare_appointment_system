package project.healthcare_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.healthcare_appointment.model.InvalidatedToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, UUID> {
    boolean existsByTokenHash(String tokenHash);

    List<InvalidatedToken> findByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM InvalidatedToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM InvalidatedToken t WHERE t.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(t) FROM InvalidatedToken t WHERE t.expiresAt > :now")
    long countActiveBlacklistedTokens(@Param("now") LocalDateTime now);

    @Query("SELECT i FROM InvalidatedToken i WHERE i.userId = :userId AND i.expiresAt > :now")
    List<InvalidatedToken> findActiveTokensByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(i) FROM InvalidatedToken i WHERE i.userId = :userId AND i.expiresAt > :now")
    long countActiveTokensByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
}
