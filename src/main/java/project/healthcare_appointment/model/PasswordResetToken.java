package project.healthcare_appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "password_reset_tokens")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @NotBlank
    String token;

    @NotNull
    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    @Column(name = "is_used")
    Boolean isUsed = false;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;
}
