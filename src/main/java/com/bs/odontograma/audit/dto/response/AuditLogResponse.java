package com.bs.odontograma.audit.dto.response;

import com.bs.odontograma.audit.entity.AuditAction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for audit log response.
 * Contains information about entity changes tracked by the audit system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Audit log entry containing entity change information")
public class AuditLogResponse {

    @Schema(description = "Audit log entry ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Tenant ID", example = "660e8400-e29b-41d4-a716-446655440000")
    private UUID tenantId;

    @Schema(description = "Type of entity that was modified", example = "Customer")
    private String entityType;

    @Schema(description = "ID of the entity that was modified", example = "770e8400-e29b-41d4-a716-446655440000")
    private UUID entityId;

    @Schema(description = "Action performed on the entity", example = "UPDATE")
    private AuditAction action;

    @Schema(description = "Email of the user who performed the action", example = "admin@example.com")
    private String userEmail;

    @Schema(description = "Timestamp when the action was performed")
    private LocalDateTime timestamp;

    @Schema(description = "IP address from which the action was performed", example = "192.168.1.1")
    private String ipAddress;

    @Schema(description = "JSON representation of the changes or entity data")
    private String changedData;
}
