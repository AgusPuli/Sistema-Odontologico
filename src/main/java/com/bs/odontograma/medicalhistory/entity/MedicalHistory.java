package com.bs.odontograma.medicalhistory.entity;

import com.bs.odontograma.medicalhistory.enums.AsaClassification;
import com.bs.odontograma.medicalhistory.enums.BloodType;
import com.bs.odontograma.medicalhistory.enums.FlossingFrequency;
import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Structured medical history / anamnesis for a patient. 1:1 with {@code Patient}.
 *
 * Design choice: keep the most-queried fields as columns (BOOLEAN/INTEGER/DATE)
 * and the rest as TEXT. No JSON columns — avoids extra Hibernate setup and keeps
 * the data queryable from SQL reports. Adding new structured fields is a
 * Flyway migration + new column here.
 */
@Entity
@Table(
        name = "medical_histories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "patient_id"}),
        indexes = {
                @Index(name = "idx_medical_histories_tenant", columnList = "tenant_id"),
                @Index(name = "idx_medical_histories_patient", columnList = "patient_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MedicalHistory extends BaseEntity {

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    // ---------- ASA / blood ----------
    @Column(name = "asa_classification", length = 10)
    @Enumerated(EnumType.STRING)
    private AsaClassification asaClassification;

    @Column(name = "blood_type", length = 15)
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    // ---------- Chronic conditions ----------
    @Column(nullable = false) @Builder.Default private Boolean diabetes = false;
    @Column(name = "diabetes_notes", columnDefinition = "TEXT") private String diabetesNotes;
    @Column(nullable = false) @Builder.Default private Boolean hypertension = false;
    @Column(name = "hypertension_notes", columnDefinition = "TEXT") private String hypertensionNotes;
    @Column(name = "heart_disease", nullable = false) @Builder.Default private Boolean heartDisease = false;
    @Column(name = "heart_disease_notes", columnDefinition = "TEXT") private String heartDiseaseNotes;
    @Column(name = "kidney_disease", nullable = false) @Builder.Default private Boolean kidneyDisease = false;
    @Column(name = "liver_disease", nullable = false)  @Builder.Default private Boolean liverDisease = false;
    @Column(name = "respiratory_disease", nullable = false) @Builder.Default private Boolean respiratoryDisease = false;
    @Column(name = "thyroid_disease", nullable = false) @Builder.Default private Boolean thyroidDisease = false;
    @Column(nullable = false) @Builder.Default private Boolean cancer = false;
    @Column(name = "bleeding_disorder", nullable = false) @Builder.Default private Boolean bleedingDisorder = false;
    @Column(name = "anticoagulant_use", nullable = false) @Builder.Default private Boolean anticoagulantUse = false;
    @Column(nullable = false) @Builder.Default private Boolean epilepsy = false;
    @Column(name = "psychiatric_condition", nullable = false) @Builder.Default private Boolean psychiatricCondition = false;
    @Column(name = "other_conditions", columnDefinition = "TEXT") private String otherConditions;

    // ---------- Allergies ----------
    @Column(name = "allergy_penicillin", nullable = false) @Builder.Default private Boolean allergyPenicillin = false;
    @Column(name = "allergy_latex", nullable = false)      @Builder.Default private Boolean allergyLatex = false;
    @Column(name = "allergy_anesthesia", nullable = false) @Builder.Default private Boolean allergyAnesthesia = false;
    @Column(name = "allergy_other", columnDefinition = "TEXT") private String allergyOther;

    // ---------- Medications ----------
    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;

    // ---------- Habits ----------
    @Column(nullable = false) @Builder.Default private Boolean smoker = false;
    @Column(name = "smoking_details", columnDefinition = "TEXT") private String smokingDetails;
    @Column(nullable = false) @Builder.Default private Boolean alcohol = false;
    @Column(name = "alcohol_details", columnDefinition = "TEXT") private String alcoholDetails;
    @Column(nullable = false) @Builder.Default private Boolean bruxism = false;

    // ---------- Female-specific ----------
    @Column(nullable = false) @Builder.Default private Boolean pregnant = false;
    @Column(name = "pregnancy_weeks") private Integer pregnancyWeeks;
    @Column(nullable = false) @Builder.Default private Boolean breastfeeding = false;

    // ---------- Dental history ----------
    @Column(name = "chief_complaint", columnDefinition = "TEXT") private String chiefComplaint;
    @Column(name = "last_dental_visit") private LocalDate lastDentalVisit;
    @Column(name = "previous_dental_problems", columnDefinition = "TEXT") private String previousDentalProblems;
    @Column(name = "brushing_per_day") private Integer brushingPerDay;
    @Column(name = "flossing_frequency", length = 20)
    @Enumerated(EnumType.STRING)
    private FlossingFrequency flossingFrequency;

    // ---------- Vital signs (last snapshot) ----------
    @Column(name = "blood_pressure_systolic") private Integer bloodPressureSystolic;
    @Column(name = "blood_pressure_diastolic") private Integer bloodPressureDiastolic;
    @Column(name = "heart_rate") private Integer heartRate;

    // ---------- Meta ----------
    @Column(name = "general_observations", columnDefinition = "TEXT")
    private String generalObservations;
    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;
}
