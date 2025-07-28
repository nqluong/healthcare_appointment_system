package project.healthcare_appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import project.healthcare_appointment.enums.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "appointments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @NotNull
    @Column(name = "appointment_date")
    LocalDate appointmentDate;

    @NotNull
    @Column(name = "start_time")
    LocalTime startTime;

    @NotNull
    @Column(name = "end_time")
    LocalTime endTime;

    @Enumerated(EnumType.STRING)
    AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    String reason;

    @Column(columnDefinition = "TEXT")
    String notes;

    @Column(name = "doctor_notes", columnDefinition = "TEXT")
    String doctorNotes;

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    BigDecimal consultationFee = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    Doctor doctor;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Payment> payments;
}
