package com.bs.odontograma.appointment.entity;

import com.bs.odontograma.appointment.enums.AppointmentStatus;
import com.bs.odontograma.auth.entity.User;
import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "appointments",
        indexes = {
                @Index(name = "idx_appointments_tenant", columnList = "tenant_id"),
                @Index(name = "idx_appointments_patient", columnList = "patient_id"),
                @Index(name = "idx_appointments_dentist", columnList = "dentist_id"),
                @Index(name = "idx_appointments_date", columnList = "appointment_date"),
                @Index(name = "idx_appointments_status", columnList = "status"),
                @Index(name = "idx_appointments_tenant_date", columnList = "tenant_id, appointment_date"),
                @Index(name = "idx_appointments_tenant_dentist_date", columnList = "tenant_id, dentist_id, appointment_date")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /**
     * Dentist (a User with role MANAGER/ADMIN). Nullable for clinics with a single
     * practitioner that don't want to track this.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentist_id")
    private User dentist;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    /**
     * Length of the slot in minutes. 30 by default; the front uses it for the calendar grid.
     */
    @Column(name = "duration_minutes", nullable = false)
    @Builder.Default
    private Integer durationMinutes = 30;

    @Column(length = 200)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    public void transitionTo(AppointmentStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new IllegalStateException(
                    "Cannot transition appointment from " + status + " to " + target);
        }
        this.status = target;
    }

    public void reschedule(LocalDate newDate, LocalTime newTime) {
        transitionTo(AppointmentStatus.RESCHEDULED);
        this.appointmentDate = newDate;
        this.appointmentTime = newTime;
        this.status = AppointmentStatus.SCHEDULED;
    }

    public boolean isPast() {
        return LocalDateTime.of(appointmentDate, appointmentTime).isBefore(LocalDateTime.now());
    }
}
