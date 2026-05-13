package com.bs.odontograma.clinicalsession.entity;

import com.bs.odontograma.appointment.entity.Appointment;
import com.bs.odontograma.auth.entity.User;
import com.bs.odontograma.clinicalsession.enums.ClinicalSessionStatus;
import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * What actually happened at a dental visit. SOAP-structured note + list of
 * procedures performed + anesthesia / materials / vital signs.
 *
 *   Appointment  → scheduled
 *   Estimate     → proposed money
 *   ClinicalSession (this) → what actually happened
 *
 * Can optionally link back to the appointment that generated it.
 */
@Entity
@Table(
        name = "clinical_sessions",
        indexes = {
                @Index(name = "idx_clinical_sessions_tenant", columnList = "tenant_id"),
                @Index(name = "idx_clinical_sessions_patient", columnList = "patient_id"),
                @Index(name = "idx_clinical_sessions_dentist", columnList = "dentist_id"),
                @Index(name = "idx_clinical_sessions_date", columnList = "session_date")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ClinicalSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dentist_id", nullable = false)
    private User dentist;

    @Column(name = "session_date", nullable = false)
    @Builder.Default
    private LocalDateTime sessionDate = LocalDateTime.now();

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // ---------- Vital signs at this session ----------
    @Column(name = "blood_pressure_systolic") private Integer bloodPressureSystolic;
    @Column(name = "blood_pressure_diastolic") private Integer bloodPressureDiastolic;
    @Column(name = "heart_rate") private Integer heartRate;

    // ---------- SOAP ----------
    @Column(columnDefinition = "TEXT") private String subjective;
    @Column(columnDefinition = "TEXT") private String objective;
    @Column(columnDefinition = "TEXT") private String assessment;
    @Column(columnDefinition = "TEXT") private String plan;

    // ---------- Anesthesia ----------
    @Column(name = "anesthesia_used", nullable = false)
    @Builder.Default
    private Boolean anesthesiaUsed = false;
    @Column(name = "anesthesia_type", length = 100) private String anesthesiaType;
    @Column(name = "anesthesia_doses") private Integer anesthesiaDoses;

    // ---------- Other ----------
    @Column(name = "materials_used", columnDefinition = "TEXT") private String materialsUsed;
    @Column(name = "general_notes", columnDefinition = "TEXT") private String generalNotes;
    @Column(name = "next_appointment_recommendation", columnDefinition = "TEXT")
    private String nextAppointmentRecommendation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ClinicalSessionStatus status = ClinicalSessionStatus.DRAFT;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ClinicalSessionProcedure> procedures = new ArrayList<>();
}
