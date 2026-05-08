package com.bs.odontograma.audit.repository;

import com.bs.odontograma.audit.entity.AuditAction;
import com.bs.odontograma.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for AuditLog entity.
 * All queries are tenant-aware for multi-tenancy support.
 * Extends JpaSpecificationExecutor for dynamic queries with optional filters.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID>, JpaSpecificationExecutor<AuditLog> {

    /**
     * Find all audit logs for a specific tenant.
     */
    Page<AuditLog> findByTenantIdOrderByTimestampDesc(UUID tenantId, Pageable pageable);

    /**
     * Find audit logs for a specific entity.
     */
    List<AuditLog> findByTenantIdAndEntityTypeAndEntityIdOrderByTimestampDesc(
            UUID tenantId,
            String entityType,
            UUID entityId
    );

    /**
     * Find audit logs by user email.
     */
    Page<AuditLog> findByTenantIdAndUserEmailOrderByTimestampDesc(
            UUID tenantId,
            String userEmail,
            Pageable pageable
    );

    /**
     * Find audit logs by action type.
     */
    Page<AuditLog> findByTenantIdAndActionOrderByTimestampDesc(
            UUID tenantId,
            AuditAction action,
            Pageable pageable
    );

    /**
     * Find audit logs by entity type.
     */
    Page<AuditLog> findByTenantIdAndEntityTypeOrderByTimestampDesc(
            UUID tenantId,
            String entityType,
            Pageable pageable
    );

    /**
     * Find audit logs within a date range.
     */
    Page<AuditLog> findByTenantIdAndTimestampBetweenOrderByTimestampDesc(
            UUID tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * Count audit logs for a tenant.
     */
    long countByTenantId(UUID tenantId);

    /**
     * Count audit logs by action type.
     */
    long countByTenantIdAndAction(UUID tenantId, AuditAction action);

    /**
     * Count audit logs for a specific entity.
     */
    long countByTenantIdAndEntityTypeAndEntityId(
            UUID tenantId,
            String entityType,
            UUID entityId
    );
}
