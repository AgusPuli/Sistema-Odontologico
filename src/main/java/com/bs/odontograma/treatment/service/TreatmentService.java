package com.bs.odontograma.treatment.service;

import com.bs.odontograma.shared.enums.DentalSpecialty;
import com.bs.odontograma.shared.exception.BusinessRuleViolationException;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.TenantContext;
import com.bs.odontograma.treatment.dto.request.CreateTreatmentRequest;
import com.bs.odontograma.treatment.dto.request.UpdateTreatmentRequest;
import com.bs.odontograma.treatment.dto.response.TreatmentResponse;
import com.bs.odontograma.treatment.entity.Treatment;
import com.bs.odontograma.treatment.mapper.TreatmentMapper;
import com.bs.odontograma.treatment.repository.TreatmentRepository;
import com.bs.odontograma.treatment.seed.DefaultDentalTreatments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TreatmentService {

    private final TreatmentRepository repository;
    private final TreatmentMapper mapper;
    private final TenantContext tenantContext;

    public TreatmentResponse create(CreateTreatmentRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        if (repository.existsByTenantIdAndCode(tenantId, request.getCode())) {
            throw new BusinessRuleViolationException("A treatment with code " + request.getCode() + " already exists");
        }
        Treatment t = Treatment.builder()
                .code(request.getCode().trim())
                .name(request.getName().trim())
                .specialty(request.getSpecialty())
                .defaultPrice(request.getDefaultPrice() != null ? request.getDefaultPrice() : BigDecimal.ZERO)
                .durationMinutes(request.getDurationMinutes())
                .description(request.getDescription())
                .active(true)
                .build();
        return mapper.toResponse(repository.save(t));
    }

    @Transactional(readOnly = true)
    public TreatmentResponse findById(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return mapper.toResponse(repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Treatment", id)));
    }

    @Transactional(readOnly = true)
    public Page<TreatmentResponse> search(DentalSpecialty specialty, String search, Pageable pageable) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.search(tenantId, specialty, search, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TreatmentResponse> findActiveBySpecialty(DentalSpecialty specialty) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantIdAndSpecialtyAndActiveTrue(tenantId, specialty)
                .stream().map(mapper::toResponse).toList();
    }

    public TreatmentResponse update(UUID id, UpdateTreatmentRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Treatment t = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Treatment", id));
        if (request.getName() != null) t.setName(request.getName().trim());
        if (request.getSpecialty() != null) t.setSpecialty(request.getSpecialty());
        if (request.getDefaultPrice() != null) t.setDefaultPrice(request.getDefaultPrice());
        if (request.getDurationMinutes() != null) t.setDurationMinutes(request.getDurationMinutes());
        if (request.getDescription() != null) t.setDescription(request.getDescription());
        return mapper.toResponse(repository.save(t));
    }

    public void deactivate(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Treatment t = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Treatment", id));
        t.deactivate();
        repository.save(t);
    }

    public void activate(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Treatment t = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Treatment", id));
        t.activate();
        repository.save(t);
    }

    /**
     * Seed the default catalog into the current tenant. Skips entries whose code
     * already exists so it can safely be called multiple times.
     */
    public int seedDefaults() {
        UUID tenantId = tenantContext.getCurrentTenantId();
        int created = 0;
        for (DefaultDentalTreatments.Item item : DefaultDentalTreatments.CATALOG) {
            if (repository.existsByTenantIdAndCode(tenantId, item.code())) continue;
            Treatment t = Treatment.builder()
                    .code(item.code())
                    .name(item.name())
                    .specialty(item.specialty())
                    .defaultPrice(item.defaultPrice())
                    .durationMinutes(item.durationMinutes())
                    .description(item.description())
                    .active(true)
                    .build();
            repository.save(t);
            created++;
        }
        log.info("Seeded {} default treatments for tenant {}", created, tenantId);
        return created;
    }
}
