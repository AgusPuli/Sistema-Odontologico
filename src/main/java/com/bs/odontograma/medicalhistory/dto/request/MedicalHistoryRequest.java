package com.bs.odontograma.medicalhistory.dto.request;

import com.bs.odontograma.medicalhistory.enums.AsaClassification;
import com.bs.odontograma.medicalhistory.enums.BloodType;
import com.bs.odontograma.medicalhistory.enums.FlossingFrequency;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

/**
 * Upsert payload for a patient's structured medical history.
 * All fields optional — partial updates are supported (null = keep existing value).
 * The endpoint upserts: if no row exists for the patient it creates one, otherwise updates.
 */
@Data
public class MedicalHistoryRequest {

    // General risk / blood
    private AsaClassification asaClassification;
    private BloodType bloodType;

    // Chronic conditions
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

    // Allergies
    private Boolean allergyPenicillin;
    private Boolean allergyLatex;
    private Boolean allergyAnesthesia;
    private String allergyOther;

    // Medications
    private String currentMedications;

    // Habits
    private Boolean smoker;
    private String smokingDetails;
    private Boolean alcohol;
    private String alcoholDetails;
    private Boolean bruxism;

    // Female-specific
    private Boolean pregnant;
    @Min(0) @Max(45) private Integer pregnancyWeeks;
    private Boolean breastfeeding;

    // Dental history & habits
    private String chiefComplaint;
    private LocalDate lastDentalVisit;
    private String previousDentalProblems;
    @Min(0) @Max(10) private Integer brushingPerDay;
    private FlossingFrequency flossingFrequency;

    // Vital signs
    @Min(50) @Max(260) private Integer bloodPressureSystolic;
    @Min(30) @Max(160) private Integer bloodPressureDiastolic;
    @Min(20) @Max(220) private Integer heartRate;

    // Free-form
    private String generalObservations;
}
