package com.bs.odontograma.periodontogram.service;

import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.patient.repository.PatientRepository;
import com.bs.odontograma.periodontogram.dto.request.CreatePeriodontogramRequest;
import com.bs.odontograma.periodontogram.dto.request.PeriodontalSiteRequest;
import com.bs.odontograma.periodontogram.dto.request.UpdatePeriodontalToothRequest;
import com.bs.odontograma.periodontogram.dto.response.PeriodontogramResponse;
import com.bs.odontograma.periodontogram.entity.Periodontogram;
import com.bs.odontograma.periodontogram.entity.PeriodontogramSite;
import com.bs.odontograma.periodontogram.entity.PeriodontogramTooth;
import com.bs.odontograma.periodontogram.enums.PeriodontalSite;
import com.bs.odontograma.periodontogram.mapper.PeriodontogramMapper;
import com.bs.odontograma.periodontogram.repository.PeriodontogramRepository;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PeriodontogramService {

    /** Full permanent dentition. We don't seed pediatric periodontograms — the
     *  clinical value is marginal in that age group and the form would be cluttered. */
    private static final int[] PERMANENT_TEETH = {
            18, 17, 16, 15, 14, 13, 12, 11,
            21, 22, 23, 24, 25, 26, 27, 28,
            48, 47, 46, 45, 44, 43, 42, 41,
            31, 32, 33, 34, 35, 36, 37, 38
    };

    /** FDIs that get a furcation reading: molars only (3rd molars optional but included). */
    private static final List<Integer> MOLARS = Arrays.asList(
            16, 17, 18, 26, 27, 28, 36, 37, 38, 46, 47, 48
    );

    private final PeriodontogramRepository repository;
    private final PatientRepository patientRepository;
    private final PeriodontogramMapper mapper;
    private final TenantContext tenantContext;

    /**
     * Idempotent. Returns the current periodontogram for the patient if there is
     * one, otherwise creates one and seeds every tooth + 6 sites with null values
     * so the form has rows ready to fill.
     */
    public PeriodontogramResponse getOrCreateCurrent(CreatePeriodontogramRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Patient patient = patientRepository.findByIdAndTenantId(request.getPatientId(), tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", request.getPatientId()));

        return repository.findByTenantIdAndPatientIdAndCurrentTrue(tenantId, patient.getId())
                .map(mapper::toResponse)
                .orElseGet(() -> create(request));
    }

    public PeriodontogramResponse create(CreatePeriodontogramRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Patient patient = patientRepository.findByIdAndTenantId(request.getPatientId(), tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", request.getPatientId()));

        // Archive any previous current periodontogram
        repository.findByTenantIdAndPatientIdAndCurrentTrue(tenantId, patient.getId())
                .ifPresent(prev -> {
                    prev.markAsHistorical();
                    repository.save(prev);
                });

        Periodontogram perio = Periodontogram.builder()
                .patient(patient)
                .examDate(request.getExamDate() != null ? request.getExamDate() : LocalDate.now())
                .generalNotes(request.getGeneralNotes())
                .current(true)
                .build();
        perio.setTenantId(tenantId);
        perio = repository.save(perio);

        // Seed teeth + 6 empty sites each
        for (int fdi : PERMANENT_TEETH) {
            PeriodontogramTooth tooth = PeriodontogramTooth.builder()
                    .periodontogram(perio)
                    .fdiNumber(fdi)
                    .build();
            tooth.setTenantId(tenantId);
            for (PeriodontalSite siteEnum : PeriodontalSite.values()) {
                PeriodontogramSite site = PeriodontogramSite.builder()
                        .tooth(tooth)
                        .site(siteEnum)
                        .bleeding(false)
                        .suppuration(false)
                        .plaque(false)
                        .build();
                site.setTenantId(tenantId);
                tooth.getSites().add(site);
            }
            perio.getTeeth().add(tooth);
        }
        perio.recomputeIndices();
        perio = repository.save(perio);

        log.info("Created periodontogram {} for patient {}", perio.getId(), patient.getId());
        return mapper.toResponse(perio);
    }

    @Transactional(readOnly = true)
    public PeriodontogramResponse findById(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return mapper.toResponse(loadOwned(id, tenantId));
    }

    @Transactional(readOnly = true)
    public PeriodontogramResponse findCurrentForPatient(UUID patientId) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantIdAndPatientIdAndCurrentTrue(tenantId, patientId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No current periodontogram for patient " + patientId));
    }

    @Transactional(readOnly = true)
    public List<PeriodontogramResponse> findAllForPatient(UUID patientId) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantIdAndPatientIdOrderByExamDateDesc(tenantId, patientId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Patch one tooth. Updates mobility/furcation/notes (when non-null) and
     * replaces the site measurements when {@code sites} is provided.
     */
    public PeriodontogramResponse updateTooth(UUID periodontogramId, Integer fdiNumber,
                                              UpdatePeriodontalToothRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Periodontogram perio = loadOwned(periodontogramId, tenantId);

        PeriodontogramTooth tooth = perio.getTeeth().stream()
                .filter(t -> fdiNumber.equals(t.getFdiNumber()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tooth " + fdiNumber + " not present in periodontogram " + periodontogramId));

        if (request.getMobility() != null) tooth.setMobility(request.getMobility());
        if (request.getFurcation() != null) {
            if (!MOLARS.contains(fdiNumber) && request.getFurcation() > 0) {
                throw new IllegalArgumentException("Furcation only applies to molars");
            }
            tooth.setFurcation(request.getFurcation());
        }
        if (request.getNotes() != null) tooth.setNotes(request.getNotes());

        if (request.getSites() != null) {
            for (PeriodontalSiteRequest siteReq : request.getSites()) {
                PeriodontogramSite site = tooth.getSites().stream()
                        .filter(s -> s.getSite() == siteReq.getSite())
                        .findFirst()
                        .orElseGet(() -> {
                            PeriodontogramSite ns = PeriodontogramSite.builder()
                                    .tooth(tooth).site(siteReq.getSite())
                                    .bleeding(false).suppuration(false).plaque(false)
                                    .build();
                            ns.setTenantId(tenantId);
                            tooth.getSites().add(ns);
                            return ns;
                        });
                if (siteReq.getProbingDepth() != null) site.setProbingDepth(siteReq.getProbingDepth());
                if (siteReq.getRecession() != null) site.setRecession(siteReq.getRecession());
                if (siteReq.getBleeding() != null) site.setBleeding(siteReq.getBleeding());
                if (siteReq.getSuppuration() != null) site.setSuppuration(siteReq.getSuppuration());
                if (siteReq.getPlaque() != null) site.setPlaque(siteReq.getPlaque());
            }
        }

        perio.recomputeIndices();
        perio = repository.save(perio);
        return mapper.toResponse(perio);
    }

    private Periodontogram loadOwned(UUID id, UUID tenantId) {
        return repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Periodontogram", id));
    }
}
