package com.bs.odontograma.shared.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Context that stores the current tenant in a ThreadLocal.
 * Provides thread-safe access to the tenant ID throughout the application.
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
     * @return UUID of the tenant, or null if not set
     */
    public UUID getCurrentTenantId() {
        return CURRENT_TENANT.get();
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
