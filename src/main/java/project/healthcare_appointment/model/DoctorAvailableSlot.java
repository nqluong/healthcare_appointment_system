package project.healthcare_appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doctor_available_slots",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"doctor_id", "slot_date", "start_time"})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorAvailableSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @NotNull
    @Column(name = "slot_date")
    LocalDate slotDate;

    @NotNull
    @Column(name = "start_time")
    LocalTime startTime;

    @NotNull
    @Column(name = "end_time")
    LocalTime endTime;

    @Column(name = "is_available")
    Boolean isAvailable = true;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    Doctor doctor;
}
