package com.bs.odontograma.tenant.service;

import com.bs.odontograma.auth.repository.UserRepository;
import com.bs.odontograma.shared.exception.BusinessRuleViolationException;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.tenant.dto.CreateTenantRequest;
import com.bs.odontograma.tenant.dto.TenantResponse;
import com.bs.odontograma.tenant.dto.UpdateTenantRequest;
import com.bs.odontograma.tenant.entity.Tenant;
import com.bs.odontograma.tenant.mapper.TenantMapper;
import com.bs.odontograma.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TenantService {

    private final TenantRepository repository;
    private final UserRepository userRepository;
    private final TenantMapper mapper;

    public TenantResponse create(CreateTenantRequest request) {
        log.info("Creating tenant: {}", request.getName());

        // Validate unique email
        if (request.getEmail() != null && repository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleViolationException(
                    "A tenant with email " + request.getEmail() + " already exists"
            );
        }

        // Validate unique tax ID if provided
        if (request.getTaxId() != null && repository.existsByTaxId(request.getTaxId())) {
            throw new BusinessRuleViolationException(
                    "A tenant with tax ID " + request.getTaxId() + " already exists"
            );
        }

        // Create tenant
        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .legalName(request.getLegalName())
                .taxId(request.getTaxId())
                .address(request.getAddress())
                .email(request.getEmail())
                .phone(request.getPhone())
                .ivaCondition(request.getIvaCondition())
                .iibb(request.getIibb())
                .activityStartDate(request.getActivityStartDate())
                .subscriptionPlan(request.getSubscriptionPlan())
                .maxUsers(request.getSubscriptionPlan().getMaxUsers())
                .active(true)
                .planExpiration(LocalDateTime.now().plusYears(1)) // 1 year default
                .build();

        tenant = repository.save(tenant);

        log.info("Tenant created: {} (ID: {})", tenant.getName(), tenant.getId());

        return mapper.toResponse(tenant);
    }

    @Transactional(readOnly = true)
    public TenantResponse findById(UUID id) {
        Tenant tenant = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tenant", id));

        return mapper.toResponse(tenant);
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> findActive() {
        return repository.findByActiveTrue().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public TenantResponse update(UUID id, UpdateTenantRequest request) {
        log.info("Updating tenant: {}", id);

        Tenant tenant = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tenant", id));

        // Validate unique email if changed
        if (request.getEmail() != null &&
                !request.getEmail().equals(tenant.getEmail()) &&
                repository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleViolationException(
                    "A tenant with email " + request.getEmail() + " already exists"
            );
        }

        // Update data
        if (request.getName() != null) {
            tenant.updateInfo(
                    request.getName(),
                    request.getLegalName(),
                    request.getEmail()
            );
        }

        // Update fiscal fields for ARCA billing
        if (request.getIvaCondition() != null) {
            tenant.setIvaCondition(request.getIvaCondition());
        }
        if (request.getIibb() != null) {
            tenant.setIibb(request.getIibb());
        }
        if (request.getActivityStartDate() != null) {
            tenant.setActivityStartDate(request.getActivityStartDate());
        }

        tenant = repository.save(tenant);

        log.info("Tenant updated: {}", id);

        return mapper.toResponse(tenant);
    }

    public void deactivate(UUID id) {
        log.info("Deactivating tenant: {}", id);

        Tenant tenant = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tenant", id));

        tenant.deactivate();
        repository.save(tenant);

        log.info("Tenant deactivated: {}", id);
    }

    public void activate(UUID id) {
        log.info("Activating tenant: {}", id);

        Tenant tenant = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tenant", id));

        tenant.activate();
        repository.save(tenant);

        log.info("Tenant activated: {}", id);
    }

    /**
     * Validates if a tenant can add a new user.
     */
    public boolean canAddUser(UUID tenantId) {
        Tenant tenant = repository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant", tenantId));

        if (!tenant.isActive()) {
            throw new BusinessRuleViolationException("Tenant is deactivated");
        }

        if (tenant.isPlanExpired()) {
            throw new BusinessRuleViolationException("Tenant plan is expired");
        }

        long currentUserCount = userRepository.countByTenantId(tenantId);

        if (!tenant.canAddUser((int) currentUserCount)) {
            throw new BusinessRuleViolationException(
                    String.format("User limit reached (%d) for plan %s",
                            tenant.getMaxUsers(), tenant.getSubscriptionPlan())
            );
        }

        return true;
    }
}