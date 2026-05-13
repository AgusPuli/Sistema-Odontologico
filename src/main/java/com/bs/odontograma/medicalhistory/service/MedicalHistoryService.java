package com.bs.odontograma.medicalhistory.service;

import com.bs.odontograma.medicalhistory.dto.request.MedicalHistoryRequest;
import com.bs.odontograma.medicalhistory.dto.response.MedicalHistoryResponse;
import com.bs.odontograma.medicalhistory.entity.MedicalHistory;
import com.bs.odontograma.medicalhistory.mapper.MedicalHistoryMapper;
import com.bs.odontograma.medicalhistory.repository.MedicalHistoryRepository;
import com.bs.odontograma.patient.repository.PatientRepository;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MedicalHistoryService {

    private final MedicalHistoryRepository repository;
    private final PatientRepository patientRepository;
    private final MedicalHistoryMapper mapper;
    private final TenantContext tenantContext;

    /**
     * Read the patient's medical history. Throws if the patient doesn't exist;
     * returns an empty history (default values) if the patient exists but no
     * history record was ever saved — that lets the frontend render the empty form.
     */
    @Transactional(readOnly = true)
    public MedicalHistoryResponse findByPatient(UUID patientId) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        // Ensure patient belongs to the tenant first
        patientRepository.findByIdAndTenantId(patientId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", patientId));

        return repository.findByTenantIdAndPatientId(tenantId, patientId)
                .map(mapper::toResponse)
                .orElseGet(() -> emptyResponse(tenantId, patientId));
    }

    /**
     * Upsert: create if missing, otherwise patch only the non-null fields of
     * the request. Touching {@code lastReviewedAt} on every save so the UI
     * can show "última revisión el ...".
     */
    public MedicalHistoryResponse upsert(UUID patientId, MedicalHistoryRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        // Ensure patient belongs to the tenant
        patientRepository.findByIdAndTenantId(patientId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", patientId));

        MedicalHistory entity = repository.findByTenantIdAndPatientId(tenantId, patientId)
                .orElseGet(() -> {
                    MedicalHistory mh = MedicalHistory.builder().patientId(patientId).build();
                    mh.setTenantId(tenantId);
                    return mh;
                });

        applyPatch(entity, request);
        entity.setLastReviewedAt(LocalDateTime.now());
        entity = repository.save(entity);

        log.info("Medical history upsert for patient {} (tenant {})", patientId, tenantId);
        return mapper.toResponse(entity);
    }

    // ---------- helpers ----------

    /**
     * Partial-update: only fields that are non-null on the request overwrite the
     * entity. Booleans always overwrite when present (false ≠ null). This lets the
     * frontend ship the whole form on every save without worrying about diffing.
     */
    private void applyPatch(MedicalHistory e, MedicalHistoryRequest r) {
        if (r.getAsaClassification() != null) e.setAsaClassification(r.getAsaClassification());
        if (r.getBloodType() != null) e.setBloodType(r.getBloodType());

        if (r.getDiabetes() != null) e.setDiabetes(r.getDiabetes());
        if (r.getDiabetesNotes() != null) e.setDiabetesNotes(r.getDiabetesNotes());
        if (r.getHypertension() != null) e.setHypertension(r.getHypertension());
        if (r.getHypertensionNotes() != null) e.setHypertensionNotes(r.getHypertensionNotes());
        if (r.getHeartDisease() != null) e.setHeartDisease(r.getHeartDisease());
        if (r.getHeartDiseaseNotes() != null) e.setHeartDiseaseNotes(r.getHeartDiseaseNotes());
        if (r.getKidneyDisease() != null) e.setKidneyDisease(r.getKidneyDisease());
        if (r.getLiverDisease() != null) e.setLiverDisease(r.getLiverDisease());
        if (r.getRespiratoryDisease() != null) e.setRespiratoryDisease(r.getRespiratoryDisease());
        if (r.getThyroidDisease() != null) e.setThyroidDisease(r.getThyroidDisease());
        if (r.getCancer() != null) e.setCancer(r.getCancer());
        if (r.getBleedingDisorder() != null) e.setBleedingDisorder(r.getBleedingDisorder());
        if (r.getAnticoagulantUse() != null) e.setAnticoagulantUse(r.getAnticoagulantUse());
        if (r.getEpilepsy() != null) e.setEpilepsy(r.getEpilepsy());
        if (r.getPsychiatricCondition() != null) e.setPsychiatricCondition(r.getPsychiatricCondition());
        if (r.getOtherConditions() != null) e.setOtherConditions(r.getOtherConditions());

        if (r.getAllergyPenicillin() != null) e.setAllergyPenicillin(r.getAllergyPenicillin());
        if (r.getAllergyLatex() != null) e.setAllergyLatex(r.getAllergyLatex());
        if (r.getAllergyAnesthesia() != null) e.setAllergyAnesthesia(r.getAllergyAnesthesia());
        if (r.getAllergyOther() != null) e.setAllergyOther(r.getAllergyOther());

        if (r.getCurrentMedications() != null) e.setCurrentMedications(r.getCurrentMedications());

        if (r.getSmoker() != null) e.setSmoker(r.getSmoker());
        if (r.getSmokingDetails() != null) e.setSmokingDetails(r.getSmokingDetails());
        if (r.getAlcohol() != null) e.setAlcohol(r.getAlcohol());
        if (r.getAlcoholDetails() != null) e.setAlcoholDetails(r.getAlcoholDetails());
        if (r.getBruxism() != null) e.setBruxism(r.getBruxism());

        if (r.getPregnant() != null) e.setPregnant(r.getPregnant());
        if (r.getPregnancyWeeks() != null) e.setPregnancyWeeks(r.getPregnancyWeeks());
        if (r.getBreastfeeding() != null) e.setBreastfeeding(r.getBreastfeeding());

        if (r.getChiefComplaint() != null) e.setChiefComplaint(r.getChiefComplaint());
        if (r.getLastDentalVisit() != null) e.setLastDentalVisit(r.getLastDentalVisit());
        if (r.getPreviousDentalProblems() != null) e.setPreviousDentalProblems(r.getPreviousDentalProblems());
        if (r.getBrushingPerDay() != null) e.setBrushingPerDay(r.getBrushingPerDay());
        if (r.getFlossingFrequency() != null) e.setFlossingFrequency(r.getFlossingFrequency());

        if (r.getBloodPressureSystolic() != null) e.setBloodPressureSystolic(r.getBloodPressureSystolic());
        if (r.getBloodPressureDiastolic() != null) e.setBloodPressureDiastolic(r.getBloodPressureDiastolic());
        if (r.getHeartRate() != null) e.setHeartRate(r.getHeartRate());

        if (r.getGeneralObservations() != null) e.setGeneralObservations(r.getGeneralObservations());
    }

    /**
     * When the patient has no medical history yet we still hand back a stub
     * so the frontend can render the empty form bound to defaults.
     */
    private MedicalHistoryResponse emptyResponse(UUID tenantId, UUID patientId) {
        return MedicalHistoryResponse.builder()
                .tenantId(tenantId)
                .patientId(patientId)
                .diabetes(false).hypertension(false).heartDisease(false)
                .kidneyDisease(false).liverDisease(false).respiratoryDisease(false)
                .thyroidDisease(false).cancer(false).bleedingDisorder(false)
                .anticoagulantUse(false).epilepsy(false).psychiatricCondition(false)
                .allergyPenicillin(false).allergyLatex(false).allergyAnesthesia(false)
                .smoker(false).alcohol(false).bruxism(false)
                .pregnant(false).breastfeeding(false)
                .build();
    }
}
