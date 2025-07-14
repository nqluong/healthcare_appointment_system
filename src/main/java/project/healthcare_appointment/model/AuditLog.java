package project.healthcare_appointment.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "audit_logs")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @NotBlank
    @Size(max = 100)
    String action;

    @NotBlank
    @Size(max = 50)
    @Column(name = "table_name")
    String tableName;

    @Column(name = "record_id")
    UUID recordId;

    @Type(JsonType.class)
    @Column(name = "old_values", columnDefinition = "jsonb")
    Map<String, Object> oldValues;

    @Type(JsonType.class)
    @Column(name = "new_values", columnDefinition = "jsonb")
    Map<String, Object> newValues;

    @Column(name = "ip_address")
    String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    String userAgent;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;
}
