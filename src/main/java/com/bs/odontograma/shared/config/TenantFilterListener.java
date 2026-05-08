package com.bs.odontograma.shared.config;

import com.bs.odontograma.shared.security.TenantContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.bs.odontograma.shared.entity.BaseEntity;

import java.util.UUID;

/**
 * JPA listener that automatically sets the tenant_id on entities.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantFilterListener {

    private final TenantContext tenantContext;

    @PrePersist
    public void setTenantOnCreate(BaseEntity entity) {
        if (entity.getTenantId() == null) {
            UUID tenantId = tenantContext.getCurrentTenantId();
            if (tenantId != null) {
                entity.setTenantId(tenantId);
                log.debug("Set tenant {} on entity {}", tenantId, entity.getClass().getSimpleName());
            } else {
                log.warn("No tenant found in context when persisting {}", entity.getClass().getSimpleName());
            }
        }
    }

    @PreUpdate
    public void validateTenantOnUpdate(BaseEntity entity) {
        UUID currentTenant = tenantContext.getCurrentTenantId();
        if (currentTenant != null && entity.getTenantId() != null) {
            if (!currentTenant.equals(entity.getTenantId())) {
                throw new SecurityException(
                        "Cannot modify entity from different tenant"
                );
            }
        }
    }
}
