package project.healthcare_appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import project.healthcare_appointment.enums.NotificationStatus;
import project.healthcare_appointment.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notifications")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @Enumerated(EnumType.STRING)
    NotificationType type;

    @NotBlank
    @Size(max = 255)
    String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    String message;

    @Enumerated(EnumType.STRING)
    NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "sent_at")
    LocalDateTime sentAt;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;
}
