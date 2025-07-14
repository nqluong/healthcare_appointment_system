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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doctors")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "license_number", unique = true)
    String licenseNumber;

    @Column(name = "years_of_experience")
    Integer yearsOfExperience = 0;

    @Column(columnDefinition = "TEXT")
    String qualification;

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    BigDecimal consultationFee = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    String bio;

    @Column(name = "is_approved")
    Boolean isApproved = true;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", referencedColumnName = "id")
    Specialty specialty;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Appointment> appointments;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<DoctorSchedule> schedules;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<DoctorAvailableSlot> availableSlots;
}
