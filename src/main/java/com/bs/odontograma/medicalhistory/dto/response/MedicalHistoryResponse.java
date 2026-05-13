package com.bs.odontograma.medicalhistory.dto.response;

import com.bs.odontograma.medicalhistory.enums.AsaClassification;
import com.bs.odontograma.medicalhistory.enums.BloodType;
import com.bs.odontograma.medicalhistory.enums.FlossingFrequency;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MedicalHistoryResponse {
    private UUID id;
    private UUID tenantId;
    private UUID patientId;

    private AsaClassification asaClassification;
    private BloodType bloodType;

    private Boolean diabetes;
    private String diabetesNotes;
    private Boolean hypertension;
    private String hypertensionNotes;
    private Boolean heartDisease;
    private String heartDiseaseNotes;
    private Boolean kidneyDisease;
    private Boolean liverDisease;
    private Boolean respiratoryDisease;
    private Boolean thyroidDisease;
    private Boolean cancer;
    private Boolean bleedingDisorder;
    private Boolean anticoagulantUse;
    private Boolean epilepsy;
    private Boolean psychiatricCondition;
    private String otherConditions;

    private Boolean allergyPenicillin;
    private Boolean allergyLatex;
    private Boolean allergyAnesthesia;
    private String allergyOther;

    private String currentMedications;

    private Boolean smoker;
    private String smokingDetails;
    private Boolean alcohol;
    private String alcoholDetails;
    private Boolean bruxism;

    private Boolean pregnant;
    private Integer pregnancyWeeks;
    private Boolean breastfeeding;

    private String chiefComplaint;
    private LocalDate lastDentalVisit;
    private String previousDentalProblems;
    private Integer brushingPerDay;
    private FlossingFrequency flossingFrequency;

    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer heartRate;

    private String generalObservations;
    private LocalDateTime lastReviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
