package project.healthcare_appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import project.healthcare_appointment.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_profiles")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name")
    String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name")
    String lastName;

    @Size(max = 20)
    String phone;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(columnDefinition = "TEXT")
    String address;

    @Size(max = 500)
    @Column(name = "avatar_url")
    String avatarUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

}
