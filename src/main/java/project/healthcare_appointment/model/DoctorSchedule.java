package project.healthcare_appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doctor_schedules",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"doctor_id", "day_of_week", "start_time"})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @NotNull
    @Min(1)
    @Max(7)
    @Column(name = "day_of_week")
    Integer dayOfWeek;

    @NotNull
    @Column(name = "start_time")
    LocalTime startTime;

    @NotNull
    @Column(name = "end_time")
    LocalTime endTime;

    @Column(name = "slot_duration")
    Integer slotDuration = 30;

    @Column(name = "is_active")
    Boolean isActive = true;

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
