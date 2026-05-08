package com.bs.odontograma.audit.service;

import com.bs.odontograma.audit.dto.response.AuditLogResponse;
import com.bs.odontograma.audit.entity.AuditAction;
import com.bs.odontograma.audit.entity.AuditLog;
import com.bs.odontograma.audit.repository.AuditLogRepository;
import com.bs.odontograma.audit.repository.AuditLogSpecifications;
import com.bs.odontograma.shared.entity.BaseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for managing audit logs.
 * Provides methods to create audit entries and query audit history.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    // Fields to exclude from change detection
    private static final Set<String> EXCLUDED_FIELDS = Set.of(
            "id", "version", "createdAt", "updatedAt", "createdBy", "updatedBy",
            "class", "handler", "hibernateLazyInitializer"
    );

    /**
     * Log entity creation.
     *
     * @param entity    The entity that was created
     * @param userEmail Email of the user who created the entity
     * @param ipAddress IP address of the request
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logCreate(BaseEntity entity, String userEmail, String ipAddress) {
        try {
            Map<String, Object> entityData = new HashMap<>();
            entityData.put("entity", serializeEntity(entity));

            AuditLog auditLog = AuditLog.builder()
                    .tenantId(entity.getTenantId())
                    .entityType(entity.getClass().getSimpleName())
                    .entityId(entity.getId())
                    .action(AuditAction.CREATE)
                    .userEmail(userEmail)
                    .ipAddress(ipAddress)
                    .timestamp(LocalDateTime.now())
                    .changedData(convertToJson(entityData))
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log created for entity {} with id {}", entity.getClass().getSimpleName(), entity.getId());
        } catch (Exception e) {
            log.error("Error creating audit log for CREATE action", e);
        }
    }

    /**
     * Log entity update with field-level changes.
     *
     * @param oldEntity Previous state of the entity
     * @param newEntity New state of the entity
     * @param userEmail Email of the user who updated the entity
     * @param ipAddress IP address of the request
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUpdate(BaseEntity oldEntity, BaseEntity newEntity, String userEmail, String ipAddress) {
        try {
            Map<String, Map<String, Object>> changes = detectChanges(oldEntity, newEntity);

            if (changes.isEmpty()) {
                log.debug("No changes detected for entity {} with id {}", newEntity.getClass().getSimpleName(), newEntity.getId());
                return;
            }

            AuditLog auditLog = AuditLog.builder()
                    .tenantId(newEntity.getTenantId())
                    .entityType(newEntity.getClass().getSimpleName())
                    .entityId(newEntity.getId())
                    .action(AuditAction.UPDATE)
                    .userEmail(userEmail)
                    .ipAddress(ipAddress)
                    .timestamp(LocalDateTime.now())
                    .changedData(convertToJson(changes))
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log created for entity {} with id {} - {} fields changed",
                    newEntity.getClass().getSimpleName(), newEntity.getId(), changes.size());
        } catch (Exception e) {
            log.error("Error creating audit log for UPDATE action", e);
        }
    }

    /**
     * Log entity deletion.
     *
     * @param entity    The entity that was deleted
     * @param userEmail Email of the user who deleted the entity
     * @param ipAddress IP address of the request
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logDelete(BaseEntity entity, String userEmail, String ipAddress) {
        try {
            Map<String, Object> entityData = new HashMap<>();
            entityData.put("entity", serializeEntity(entity));

            AuditLog auditLog = AuditLog.builder()
                    .tenantId(entity.getTenantId())
                    .entityType(entity.getClass().getSimpleName())
                    .entityId(entity.getId())
                    .action(AuditAction.DELETE)
                    .userEmail(userEmail)
                    .ipAddress(ipAddress)
                    .timestamp(LocalDateTime.now())
                    .changedData(convertToJson(entityData))
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log created for entity {} with id {}", entity.getClass().getSimpleName(), entity.getId());
        } catch (Exception e) {
            log.error("Error creating audit log for DELETE action", e);
        }
    }

    /**
     * Search audit logs with multiple filters.
     * Uses JPA Specifications for dynamic query building with optional filters.
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> searchAuditLogs(
            UUID tenantId,
            String entityType,
            UUID entityId,
            String userEmail,
            AuditAction action,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        Specification<AuditLog> spec = AuditLogSpecifications.withFilters(
                tenantId, entityType, entityId, userEmail, action, startDate, endDate
        );
        Page<AuditLog> auditLogs = auditLogRepository.findAll(spec, pageable);
        return auditLogs.map(this::mapToResponse);
    }

    /**
     * Get audit history for a specific entity.
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getEntityHistory(UUID tenantId, String entityType, UUID entityId) {
        List<AuditLog> auditLogs = auditLogRepository.findByTenantIdAndEntityTypeAndEntityIdOrderByTimestampDesc(
                tenantId, entityType, entityId
        );
        return auditLogs.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Get audit logs for a specific tenant.
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogsByTenant(UUID tenantId, Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogRepository.findByTenantIdOrderByTimestampDesc(tenantId, pageable);
        return auditLogs.map(this::mapToResponse);
    }

    /**
     * Detect changes between two entity states.
     * Returns a map of field names to their old and new values.
     */
    private Map<String, Map<String, Object>> detectChanges(Object oldEntity, Object newEntity) {
        Map<String, Map<String, Object>> changes = new HashMap<>();

        if (oldEntity == null || newEntity == null) {
            return changes;
        }

        Class<?> entityClass = oldEntity.getClass();
        List<Field> fields = getAllFields(entityClass);

        for (Field field : fields) {
            if (EXCLUDED_FIELDS.contains(field.getName())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object oldValue = field.get(oldEntity);
                Object newValue = field.get(newEntity);

                if (!Objects.equals(oldValue, newValue)) {
                    Map<String, Object> change = new HashMap<>();
                    change.put("old", serializeValue(oldValue));
                    change.put("new", serializeValue(newValue));
                    changes.put(field.getName(), change);
                }
            } catch (IllegalAccessException e) {
                log.warn("Could not access field {} for change detection", field.getName(), e);
            }
        }

        return changes;
    }

    /**
     * Get all fields from a class including inherited fields.
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * Serialize entity to a map representation.
     */
    private Map<String, Object> serializeEntity(Object entity) {
        Map<String, Object> result = new HashMap<>();
        List<Field> fields = getAllFields(entity.getClass());

        for (Field field : fields) {
            if (EXCLUDED_FIELDS.contains(field.getName())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object value = field.get(entity);
                result.put(field.getName(), serializeValue(value));
            } catch (IllegalAccessException e) {
                log.warn("Could not access field {} for serialization", field.getName(), e);
            }
        }

        return result;
    }

    /**
     * Serialize a value to a JSON-compatible format.
     */
    private Object serializeValue(Object value) {
        if (value == null) {
            return null;
        }

        // Handle collections
        if (value instanceof Collection) {
            return value.toString(); // Simplified - could be expanded to handle nested objects
        }

        // Handle entities (to avoid circular references)
        if (value instanceof BaseEntity) {
            return ((BaseEntity) value).getId().toString();
        }

        // Handle UUIDs
        if (value instanceof UUID) {
            return value.toString();
        }

        // Handle enums
        if (value.getClass().isEnum()) {
            return value.toString();
        }

        // Primitive types and strings
        return value;
    }

    /**
     * Convert object to JSON string.
     */
    private String convertToJson(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON", e);
            return "{}";
        }
    }

    /**
     * Map AuditLog entity to response DTO.
     */
    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .tenantId(auditLog.getTenantId())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .action(auditLog.getAction())
                .userEmail(auditLog.getUserEmail())
                .timestamp(auditLog.getTimestamp())
                .ipAddress(auditLog.getIpAddress())
                .changedData(auditLog.getChangedData())
                .build();
    }
}
