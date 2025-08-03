package project.healthcare_appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patients")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    String medicalHistory;

    @Column(columnDefinition = "TEXT")
    String allergies;

    @Size(max = 5)
    @Column(name = "blood_type")
    String bloodType;

    @Size(max = 100)
    @Column(name = "emergency_contact_name")
    String emergencyContactName;

    @Size(max = 20)
    @Column(name = "emergency_contact_phone")
    String emergencyContactPhone;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Appointment> appointments;
}
