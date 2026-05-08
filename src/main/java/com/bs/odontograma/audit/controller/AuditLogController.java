package com.bs.odontograma.audit.controller;

import com.bs.odontograma.audit.dto.response.AuditLogResponse;
import com.bs.odontograma.audit.entity.AuditAction;
import com.bs.odontograma.audit.service.AuditService;
import com.bs.odontograma.shared.dto.ApiResponse;
import com.bs.odontograma.shared.security.TenantContext;
import com.bs.odontograma.shared.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for audit logs.
 * Provides endpoints for administrators to query and review system audit trails.
 */
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Audit log query endpoints for administrators")
public class AuditLogController {

    private final AuditService auditService;
    private final TenantContext tenantContext;

    /**
     * Search audit logs with multiple filters.
     * Only accessible by administrators.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Search audit logs",
            description = "Search audit logs with optional filters. Returns paginated results ordered by timestamp descending."
    )
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> searchAuditLogs(
            @Parameter(description = "Entity type to filter by (e.g., Customer, WorkOrder)")
            @RequestParam(required = false) String entityType,

            @Parameter(description = "Specific entity ID to filter by")
            @RequestParam(required = false) UUID entityId,

            @Parameter(description = "User email to filter by")
            @RequestParam(required = false) String userEmail,

            @Parameter(description = "Action type to filter by (CREATE, UPDATE, DELETE)")
            @RequestParam(required = false) AuditAction action,

            @Parameter(description = "Start date for filtering (inclusive)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "End date for filtering (inclusive)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        UUID tenantId = getTenantId();

        Page<AuditLogResponse> auditLogs = auditService.searchAuditLogs(
                tenantId,
                entityType,
                entityId,
                userEmail,
                action,
                startDate,
                endDate,
                pageable
        );

        return ResponseEntity.ok(ApiResponse.success(auditLogs));
    }

    /**
     * Get audit history for a specific entity.
     * Returns all audit log entries for the given entity.
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Get entity audit history",
            description = "Get complete audit history for a specific entity. Returns all changes ordered by timestamp descending."
    )
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getEntityHistory(
            @Parameter(description = "Type of entity (e.g., Customer, WorkOrder)")
            @PathVariable String entityType,

            @Parameter(description = "ID of the entity")
            @PathVariable UUID entityId
    ) {
        UUID tenantId = getTenantId();

        List<AuditLogResponse> history = auditService.getEntityHistory(
                tenantId,
                entityType,
                entityId
        );

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * Get all audit logs for the current tenant.
     */
    @GetMapping("/tenant")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Get tenant audit logs",
            description = "Get all audit logs for the current tenant. Returns paginated results ordered by timestamp descending."
    )
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getTenantAuditLogs(
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        UUID tenantId = getTenantId();

        Page<AuditLogResponse> auditLogs = auditService.getAuditLogsByTenant(tenantId, pageable);

        return ResponseEntity.ok(ApiResponse.success(auditLogs));
    }

    /**
     * Get current tenant ID from security context.
     */
    private UUID getTenantId() {
        UUID tenantId = tenantContext.getCurrentTenantId();

        if (tenantId == null) {
            // Fallback: get from authentication if TenantContext is null
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                tenantId = userPrincipal.getTenantId();
            }
        }

        return tenantId;
    }
}
