package com.bs.odontograma.audit.listener;

import com.bs.odontograma.audit.service.AuditService;
import com.bs.odontograma.shared.entity.BaseEntity;
import com.bs.odontograma.shared.security.UserPrincipal;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * JPA Entity Listener for auditing entity changes.
 * Captures CREATE, UPDATE, and DELETE operations and logs them via AuditService.
 *
 * For UPDATE operations, uses Hibernate's internal API to capture the database state
 * before modifications are applied.
 */
@Component
@Slf4j
public class AuditListener {

    // ThreadLocal to store entity snapshots before updates
    private static final ThreadLocal<Map<String, BaseEntity>> ENTITY_SNAPSHOTS =
            ThreadLocal.withInitial(HashMap::new);

    // Static reference to Spring context (set by AuditListenerConfig)
    private static ApplicationContext applicationContext;

    /**
     * Set the application context (called by configuration class).
     */
    public static void setApplicationContext(ApplicationContext context) {
        AuditListener.applicationContext = context;
    }

    /**
     * Called after entity is created.
     */
    @PostPersist
    public void onEntityCreated(BaseEntity entity) {
        try {
            AuditService auditService = getAuditService();
            if (auditService != null) {
                String userEmail = getCurrentUserEmail();
                String ipAddress = getClientIpAddress();
                auditService.logCreate(entity, userEmail, ipAddress);
            }
        } catch (Exception e) {
            log.error("Error in audit listener for CREATE on entity {}",
                    entity.getClass().getSimpleName(), e);
        }
    }

    /**
     * Called before entity is updated - capture the old state from Hibernate.
     * Uses Hibernate's SessionImplementor to get the loaded (database) state.
     */
    @PreUpdate
    public void captureOldState(BaseEntity entity) {
        try {
            EntityManager em = getEntityManager();
            if (em != null && em.getDelegate() instanceof SessionImplementor) {
                SessionImplementor session = (SessionImplementor) em.getDelegate();
                EntityEntry entry = session.getPersistenceContext().getEntry(entity);

                if (entry != null && entry.getLoadedState() != null) {
                    // Create a snapshot using the loaded (pre-modification) state
                    BaseEntity snapshot = createSnapshotFromLoadedState(entity, entry);
                    if (snapshot != null) {
                        ENTITY_SNAPSHOTS.get().put(entity.getId().toString(), snapshot);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error capturing old state for entity {}",
                    entity.getClass().getSimpleName(), e);
        }
    }

    /**
     * Creates a snapshot of the entity using Hibernate's loaded state.
     */
    private BaseEntity createSnapshotFromLoadedState(BaseEntity entity, EntityEntry entry) {
        try {
            // Create a new instance of the same class
            var constructor = entity.getClass().getDeclaredConstructor();
            constructor.setAccessible(true);  // Make constructor accessible even if protected/private
            @SuppressWarnings("unchecked")
            BaseEntity snapshot = (BaseEntity) constructor.newInstance();

            // Get property names and loaded values
            Object[] loadedState = entry.getLoadedState();
            String[] propertyNames = entry.getPersister().getPropertyNames();

            // Set the ID field
            snapshot.setId(entity.getId());
            snapshot.setTenantId(entity.getTenantId());

            // Map loaded state to snapshot
            for (int i = 0; i < propertyNames.length; i++) {
                String propertyName = propertyNames[i];
                Object loadedValue = loadedState[i];

                try {
                    Field field = findField(entity.getClass(), propertyName);
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(snapshot, loadedValue);
                    }
                } catch (Exception e) {
                    log.debug("Could not set property {} on snapshot", propertyName, e);
                }
            }

            return snapshot;
        } catch (Exception e) {
            log.error("Error creating snapshot from loaded state", e);
            return null;
        }
    }

    /**
     * Find a field by name in the class hierarchy.
     */
    private Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Called after entity is updated.
     */
    @PostUpdate
    public void onEntityUpdated(BaseEntity entity) {
        try {
            BaseEntity oldEntity = ENTITY_SNAPSHOTS.get().remove(entity.getId().toString());

            if (oldEntity != null) {
                AuditService auditService = getAuditService();
                if (auditService != null) {
                    String userEmail = getCurrentUserEmail();
                    String ipAddress = getClientIpAddress();
                    auditService.logUpdate(oldEntity, entity, userEmail, ipAddress);
                }
            }
        } catch (Exception e) {
            log.error("Error in audit listener for UPDATE on entity {}",
                    entity.getClass().getSimpleName(), e);
        } finally {
            // Clean up ThreadLocal
            ENTITY_SNAPSHOTS.get().remove(entity.getId().toString());
        }
    }

    /**
     * Called before entity is deleted.
     */
    @PreRemove
    public void onEntityDeleted(BaseEntity entity) {
        try {
            AuditService auditService = getAuditService();
            if (auditService != null) {
                String userEmail = getCurrentUserEmail();
                String ipAddress = getClientIpAddress();
                auditService.logDelete(entity, userEmail, ipAddress);
            }
        } catch (Exception e) {
            log.error("Error in audit listener for DELETE on entity {}",
                    entity.getClass().getSimpleName(), e);
        }
    }

    /**
     * Get current user email from security context.
     */
    private String getCurrentUserEmail() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return "SYSTEM";
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal) {
                return ((UserPrincipal) principal).getEmail();
            }

            return authentication.getName();
        } catch (Exception e) {
            log.debug("Could not get current user email", e);
            return "UNKNOWN";
        }
    }

    /**
     * Get client IP address from HTTP request.
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();

            // Check for X-Forwarded-For header (proxy/load balancer)
            String ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("X-Real-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }

            // X-Forwarded-For can contain multiple IPs, take the first one
            if (ipAddress != null && ipAddress.contains(",")) {
                ipAddress = ipAddress.split(",")[0].trim();
            }

            return ipAddress;
        } catch (Exception e) {
            log.debug("Could not get client IP address", e);
            return null;
        }
    }

    /**
     * Get AuditService bean from Spring context.
     */
    private AuditService getAuditService() {
        try {
            if (applicationContext != null) {
                return applicationContext.getBean(AuditService.class);
            }
        } catch (Exception e) {
            log.error("Could not get AuditService bean", e);
        }
        return null;
    }

    /**
     * Get EntityManager from Spring context.
     */
    private EntityManager getEntityManager() {
        try {
            if (applicationContext != null) {
                return applicationContext.getBean(EntityManager.class);
            }
        } catch (Exception e) {
            log.debug("Could not get EntityManager bean", e);
        }
        return null;
    }

    /**
     * Clean up ThreadLocal when thread is done.
     */
    public static void cleanup() {
        ENTITY_SNAPSHOTS.remove();
    }
}
