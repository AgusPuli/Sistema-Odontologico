package com.bs.odontograma.odontogram.service;

import com.bs.odontograma.odontogram.dto.request.CreateOdontogramRequest;
import com.bs.odontograma.odontogram.dto.request.UpdateSurfaceRequest;
import com.bs.odontograma.odontogram.dto.request.UpdateToothRequest;
import com.bs.odontograma.odontogram.dto.response.OdontogramResponse;
import com.bs.odontograma.odontogram.dto.response.ToothHistoryResponse;
import com.bs.odontograma.odontogram.entity.Odontogram;
import com.bs.odontograma.odontogram.entity.ToothHistoryEntry;
import com.bs.odontograma.odontogram.entity.ToothRecord;
import com.bs.odontograma.odontogram.entity.ToothSurfaceCondition;
import com.bs.odontograma.odontogram.enums.Dentition;
import com.bs.odontograma.odontogram.enums.ToothCondition;
import com.bs.odontograma.odontogram.enums.TreatmentStatus;
import com.bs.odontograma.odontogram.mapper.OdontogramMapper;
import com.bs.odontograma.odontogram.repository.OdontogramRepository;
import com.bs.odontograma.odontogram.repository.ToothHistoryEntryRepository;
import com.bs.odontograma.odontogram.repository.ToothRecordRepository;
import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.patient.repository.PatientRepository;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.TenantContext;
import com.bs.odontograma.shared.security.UserPrincipal;
import com.bs.odontograma.treatment.entity.Treatment;
import com.bs.odontograma.treatment.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OdontogramService {

    // FDI numbering for permanent dentition: 4 quadrants of 8 teeth each
    private static final int[] PERMANENT_TEETH = {
            18, 17, 16, 15, 14, 13, 12, 11,
            21, 22, 23, 24, 25, 26, 27, 28,
            48, 47, 46, 45, 44, 43, 42, 41,
            31, 32, 33, 34, 35, 36, 37, 38
    };

    // FDI numbering for primary dentition: 4 quadrants of 5 teeth each
    private static final int[] PRIMARY_TEETH = {
            55, 54, 53, 52, 51,
            61, 62, 63, 64, 65,
            85, 84, 83, 82, 81,
            71, 72, 73, 74, 75
    };

    private final OdontogramRepository repository;
    private final ToothRecordRepository toothRepository;
    private final ToothHistoryEntryRepository historyRepository;
    private final PatientRepository patientRepository;
    private final TreatmentRepository treatmentRepository;
    private final OdontogramMapper mapper;
    private final TenantContext tenantContext;

    /**
     * Get-or-create the current odontogram for a patient. Idempotent: if a current
     * odontogram already exists for the patient, returns it without modifications.
     * Use this from the frontend's first-render flow so opening the chart never
     * accidentally archives the patient's existing data.
     */
    public OdontogramResponse getOrCreateCurrent(CreateOdontogramRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Patient patient = patientRepository.findByIdAndTenantId(request.getPatientId(), tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", request.getPatientId()));

        return repository.findByTenantIdAndPatientIdAndCurrentTrue(tenantId, patient.getId())
                .map(mapper::toResponse)
                .orElseGet(() -> create(request));
    }

    public OdontogramResponse create(CreateOdontogramRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        Patient patient = patientRepository.findByIdAndTenantId(request.getPatientId(), tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", request.getPatientId()));

        // Mark previous "current" odontogram as historical so the new one becomes the active snapshot.
        repository.findByTenantIdAndPatientIdAndCurrentTrue(tenantId, patient.getId())
                .ifPresent(prev -> {
                    prev.markAsHistorical();
                    repository.save(prev);
                });

        Odontogram odontogram = Odontogram.builder()
                .patient(patient)
                .dentition(request.getDentition())
                .generalNotes(request.getGeneralNotes())
                .current(true)
                .build();
        odontogram = repository.save(odontogram);

        // Seed all teeth as HEALTHY so the frontend has the full grid from the start
        for (int fdi : pickTeeth(request.getDentition())) {
            ToothRecord tooth = ToothRecord.builder()
                    .odontogram(odontogram)
                    .fdiNumber(fdi)
                    .condition(ToothCondition.HEALTHY)
                    .build();
            odontogram.getTeeth().add(tooth);
        }
        odontogram = repository.save(odontogram);

        return mapper.toResponse(odontogram);
    }

    @Transactional(readOnly = true)
    public OdontogramResponse findById(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return mapper.toResponse(loadOwned(id, tenantId));
    }

    @Transactional(readOnly = true)
    public OdontogramResponse findCurrentForPatient(UUID patientId) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Odontogram odontogram = repository.findByTenantIdAndPatientIdAndCurrentTrue(tenantId, patientId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No current odontogram for patient " + patientId
                ));
        return mapper.toResponse(odontogram);
    }

    @Transactional(readOnly = true)
    public List<OdontogramResponse> findAllForPatient(UUID patientId) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantIdAndPatientIdOrderByCreatedAtDesc(tenantId, patientId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public OdontogramResponse updateTooth(UUID odontogramId, Integer fdiNumber, UpdateToothRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Odontogram odontogram = loadOwned(odontogramId, tenantId);

        ToothRecord tooth = toothRepository.findByOdontogramIdAndFdiNumber(odontogram.getId(), fdiNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tooth " + fdiNumber + " not present in odontogram " + odontogramId
                ));

        tooth.setCondition(request.getCondition());
        tooth.setObservation(request.getObservation());

        // Replace surface findings entirely on every update — keeps the API simple
        tooth.getSurfaces().clear();
        if (request.getSurfaces() != null) {
            for (UpdateSurfaceRequest s : request.getSurfaces()) {
                tooth.getSurfaces().add(ToothSurfaceCondition.builder()
                        .toothRecord(tooth)
                        .surface(s.getSurface())
                        .condition(s.getCondition())
                        .notes(s.getNotes())
                        .build());
            }
        }

        Treatment treatment = null;
        if (request.getTreatmentId() != null) {
            treatment = treatmentRepository.findByIdAndTenantId(request.getTreatmentId(), tenantId)
                    .orElseThrow(() -> new EntityNotFoundException("Treatment", request.getTreatmentId()));
        }

        // Append a clinical history entry for this change
        ToothHistoryEntry entry = ToothHistoryEntry.builder()
                .toothRecord(tooth)
                .finding(request.getCondition())
                .surface(request.getSurfaces() != null && !request.getSurfaces().isEmpty()
                        ? request.getSurfaces().get(0).getSurface()
                        : null)
                .treatmentStatus(request.getTreatmentStatus() != null
                        ? request.getTreatmentStatus()
                        : TreatmentStatus.PENDING)
                .note(request.getHistoryNote())
                .recordedByEmail(currentUserEmail())
                .treatment(treatment)
                .build();
        tooth.getHistory().add(entry);

        toothRepository.save(tooth);
        return mapper.toResponse(odontogram);
    }

    @Transactional(readOnly = true)
    public List<ToothHistoryResponse> getToothHistory(UUID odontogramId, Integer fdiNumber) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        loadOwned(odontogramId, tenantId);
        ToothRecord tooth = toothRepository.findByOdontogramIdAndFdiNumber(odontogramId, fdiNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tooth " + fdiNumber + " not present in odontogram " + odontogramId));
        return historyRepository.findByToothRecordIdOrderByCreatedAtDesc(tooth.getId())
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ToothHistoryResponse> getTreatmentPlan(UUID odontogramId, TreatmentStatus status) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        loadOwned(odontogramId, tenantId);
        return historyRepository.findPlanForOdontogram(odontogramId, status)
                .stream().map(mapper::toResponse).toList();
    }

    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal up) {
            return up.getEmail();
        }
        return "SYSTEM";
    }

    private Odontogram loadOwned(UUID id, UUID tenantId) {
        return repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Odontogram", id));
    }

    private int[] pickTeeth(Dentition dentition) {
        return switch (dentition) {
            case PERMANENT -> PERMANENT_TEETH;
            case PRIMARY -> PRIMARY_TEETH;
            case MIXED -> mergeArrays(PERMANENT_TEETH, PRIMARY_TEETH);
        };
    }

    private int[] mergeArrays(int[] a, int[] b) {
        int[] out = new int[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }
}
