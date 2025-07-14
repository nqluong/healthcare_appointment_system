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
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "specialties")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "is_active")
    Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Doctor> doctors;
}
