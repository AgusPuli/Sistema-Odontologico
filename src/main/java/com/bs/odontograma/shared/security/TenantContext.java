package com.bs.odontograma.shared.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Context that stores the current tenant in a ThreadLocal.
 * Resolution order:
 *   1. ThreadLocal (populated by JwtAuthenticationFilter from JWT claim)
 *   2. SecurityContextHolder → UserPrincipal.tenantId (fallback when ThreadLocal is empty)
 */
@Component
@Slf4j
public class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * Sets the current tenant for the active thread.
     */
    public void setCurrentTenantId(UUID tenantId) {
        log.debug("Setting tenant: {}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Retrieves the current tenant for the active thread.
     * Falls back to the authenticated principal when the ThreadLocal is empty
     * (e.g. when the JWT did not carry a tenantId claim or the filter did not run).
     *
     * @return UUID of the tenant, or null if not resolvable
     */
    public UUID getCurrentTenantId() {
        UUID fromThread = CURRENT_TENANT.get();
        if (fromThread != null) {
            return fromThread;
        }

        // Fallback: read from the authenticated principal
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                UUID fromPrincipal = principal.getTenantId();
                if (fromPrincipal != null) {
                    log.debug("Resolved tenant {} from SecurityContextHolder (fallback)", fromPrincipal);
                    CURRENT_TENANT.set(fromPrincipal); // cache for subsequent calls on this thread
                    return fromPrincipal;
                }
            }
        } catch (Exception e) {
            log.warn("Could not resolve tenant from SecurityContextHolder: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Clears the tenant from the current thread.
     */
    public void clear() {
        log.debug("Clearing tenant context");
        CURRENT_TENANT.remove();
    }

    /**
     * Checks whether a tenant is currently set.
     */
    public boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
}
