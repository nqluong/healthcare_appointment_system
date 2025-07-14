package project.healthcare_appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import project.healthcare_appointment.enums.PaymentMethod;
import project.healthcare_appointment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @NotNull
    @Column(precision = 10, scale = 2)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    PaymentMethod paymentMethod = PaymentMethod.CASH;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Size(max = 100)
    @Column(name = "transaction_id")
    String transactionId;

    @Column(name = "payment_date")
    LocalDateTime paymentDate;

    @Column(columnDefinition = "TEXT")
    String notes;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", referencedColumnName = "id")
    Appointment appointment;
}
