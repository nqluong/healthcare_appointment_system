package project.healthcare_appointment.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import project.healthcare_appointment.enums.TokenType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invalidated_tokens")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidatedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    String tokenHash; // SHA-256 hash của token để bảo mật

    @Column(name = "user_id", nullable = false)
    UUID userId;

    @Column(name = "token_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    TokenType tokenType;

    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "black_listed_at")
    LocalDateTime blacklistedAt;

    @Column(name = "reason", length = 50)
    String reason; // "LOGOUT", "REFRESH", "SECURITY_BREACH", etc.

    @Column(name = "ip_address", length = 45)
    String ipAddress;

    @Column(name="user_agent",length = 500)
    String userAgent;
}
