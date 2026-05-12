package com.bs.odontograma.shared.config;

import com.bs.odontograma.shared.security.TenantContext;
import com.bs.odontograma.shared.security.UserPrincipal;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.bs.odontograma.shared.entity.BaseEntity;

import java.util.UUID;

/**
 * JPA listener that automatically sets the tenant_id on entities.
 * Resolution order:
 *  1. TenantContext (set by JwtAuthenticationFilter from the JWT claim)
 *  2. SecurityContextHolder → UserPrincipal.tenantId (fallback)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantFilterListener {

    private final TenantContext tenantContext;

    @PrePersist
    public void setTenantOnCreate(BaseEntity entity) {
        if (entity.getTenantId() == null) {
            UUID tenantId = resolveTenantId();
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
        UUID currentTenant = resolveTenantId();
        if (currentTenant != null && entity.getTenantId() != null) {
            if (!currentTenant.equals(entity.getTenantId())) {
                throw new SecurityException(
                        "Cannot modify entity from different tenant"
                );
            }
        }
    }

    /**
     * Returns the active tenant UUID.
     * Prefers TenantContext (populated by JwtAuthenticationFilter).
     * Falls back to the UserPrincipal stored in SecurityContextHolder so that
     * the tenant is always resolved even when the X-Tenant-ID header is absent.
     */
    private UUID resolveTenantId() {
        UUID fromContext = tenantContext.getCurrentTenantId();
        if (fromContext != null) {
            return fromContext;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            UUID fromPrincipal = principal.getTenantId();
            if (fromPrincipal != null) {
                log.debug("Resolved tenant {} from SecurityContextHolder (fallback)", fromPrincipal);
                // Populate the context so subsequent accesses in the same thread hit the fast path
                tenantContext.setCurrentTenantId(fromPrincipal);
                return fromPrincipal;
            }
        }

        return null;
    }
}
