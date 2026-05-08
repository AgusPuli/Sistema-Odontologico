package com.bs.odontograma.treatment.service;

import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.patient.repository.PatientRepository;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.TenantContext;
import com.bs.odontograma.treatment.dto.request.CreateEstimateRequest;
import com.bs.odontograma.treatment.dto.request.EstimateItemRequest;
import com.bs.odontograma.treatment.dto.response.EstimateResponse;
import com.bs.odontograma.treatment.entity.Estimate;
import com.bs.odontograma.treatment.entity.EstimateItem;
import com.bs.odontograma.treatment.entity.Treatment;
import com.bs.odontograma.treatment.enums.EstimateStatus;
import com.bs.odontograma.treatment.mapper.TreatmentMapper;
import com.bs.odontograma.treatment.repository.EstimateRepository;
import com.bs.odontograma.treatment.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EstimateService {

    private final EstimateRepository repository;
    private final PatientRepository patientRepository;
    private final TreatmentRepository treatmentRepository;
    private final TreatmentMapper mapper;
    private final TenantContext tenantContext;

    public EstimateResponse create(CreateEstimateRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        Patient patient = patientRepository.findByIdAndTenantId(request.getPatientId(), tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", request.getPatientId()));

        Estimate estimate = Estimate.builder()
                .patient(patient)
                .issueDate(LocalDate.now())
                .validUntil(request.getValidUntil())
                .notes(request.getNotes())
                .status(EstimateStatus.DRAFT)
                .total(BigDecimal.ZERO)
                .build();

        for (EstimateItemRequest itemReq : request.getItems()) {
            Treatment t = treatmentRepository.findByIdAndTenantId(itemReq.getTreatmentId(), tenantId)
                    .orElseThrow(() -> new EntityNotFoundException("Treatment", itemReq.getTreatmentId()));
            BigDecimal price = itemReq.getUnitPrice() != null
                    ? itemReq.getUnitPrice()
                    : (t.getDefaultPrice() != null ? t.getDefaultPrice() : BigDecimal.ZERO);
            int qty = itemReq.getQuantity() != null ? itemReq.getQuantity() : 1;

            EstimateItem item = EstimateItem.builder()
                    .estimate(estimate)
                    .treatment(t)
                    .fdiNumber(itemReq.getFdiNumber())
                    .quantity(qty)
                    .unitPrice(price)
                    .build();
            item.recomputeSubtotal();
            estimate.getItems().add(item);
        }
        estimate.recomputeTotal();
        return mapper.toResponse(repository.save(estimate));
    }

    @Transactional(readOnly = true)
    public EstimateResponse findById(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return mapper.toResponse(repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Estimate", id)));
    }

    @Transactional(readOnly = true)
    public Page<EstimateResponse> findAll(Pageable pageable) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantId(tenantId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<EstimateResponse> findByPatient(UUID patientId) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantIdAndPatientIdOrderByIssueDateDesc(tenantId, patientId)
                .stream().map(mapper::toResponse).toList();
    }

    public EstimateResponse changeStatus(UUID id, EstimateStatus newStatus) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Estimate e = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Estimate", id));
        e.setStatus(newStatus);
        return mapper.toResponse(repository.save(e));
    }

    /**
     * Replaces the items of an existing estimate. Items are mutated in place
     * (clear + repopulate via cascade orphanRemoval) so the parent ID is preserved.
     * Status is reset to DRAFT to avoid sneaky changes on already-accepted estimates.
     */
    public EstimateResponse update(UUID id, CreateEstimateRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Estimate estimate = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Estimate", id));

        if (estimate.getStatus() == EstimateStatus.ACCEPTED ||
            estimate.getStatus() == EstimateStatus.CANCELLED) {
            throw new com.bs.odontograma.shared.exception.BusinessRuleViolationException(
                    "Cannot modify an estimate that is " + estimate.getStatus()
            );
        }

        estimate.setValidUntil(request.getValidUntil());
        estimate.setNotes(request.getNotes());
        estimate.getItems().clear();

        for (com.bs.odontograma.treatment.dto.request.EstimateItemRequest itemReq : request.getItems()) {
            com.bs.odontograma.treatment.entity.Treatment t = treatmentRepository
                    .findByIdAndTenantId(itemReq.getTreatmentId(), tenantId)
                    .orElseThrow(() -> new EntityNotFoundException("Treatment", itemReq.getTreatmentId()));
            BigDecimal price = itemReq.getUnitPrice() != null
                    ? itemReq.getUnitPrice()
                    : (t.getDefaultPrice() != null ? t.getDefaultPrice() : BigDecimal.ZERO);
            int qty = itemReq.getQuantity() != null ? itemReq.getQuantity() : 1;

            com.bs.odontograma.treatment.entity.EstimateItem item =
                    com.bs.odontograma.treatment.entity.EstimateItem.builder()
                            .estimate(estimate)
                            .treatment(t)
                            .fdiNumber(itemReq.getFdiNumber())
                            .quantity(qty)
                            .unitPrice(price)
                            .build();
            item.recomputeSubtotal();
            estimate.getItems().add(item);
        }
        estimate.recomputeTotal();
        return mapper.toResponse(repository.save(estimate));
    }

    public void delete(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Estimate e = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Estimate", id));
        if (e.getStatus() == EstimateStatus.ACCEPTED) {
            throw new com.bs.odontograma.shared.exception.BusinessRuleViolationException(
                    "Cannot delete an accepted estimate. Cancel it instead."
            );
        }
        repository.delete(e);
    }
}
