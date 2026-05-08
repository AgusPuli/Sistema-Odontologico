-- V3: Create audit_log table (Audit feature)
-- Tracks all CREATE, UPDATE, DELETE operations on tenant-owned entities.
-- Does NOT extend BaseEntity and has no FK to tenants to avoid coupling
-- and to allow audit entries to survive tenant lifecycle events.

CREATE TABLE audit_log (
    id          UUID        NOT NULL,
    tenant_id   UUID        NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    entity_id   UUID        NOT NULL,
    action      VARCHAR(20) NOT NULL,
    user_email  VARCHAR(255),
    timestamp   TIMESTAMP   NOT NULL,
    ip_address  VARCHAR(50),
    changed_data TEXT,

    CONSTRAINT pk_audit_log PRIMARY KEY (id),
    CONSTRAINT chk_audit_log_action CHECK (action IN ('CREATE', 'UPDATE', 'DELETE'))
);

CREATE INDEX idx_audit_tenant    ON audit_log (tenant_id);
CREATE INDEX idx_audit_entity    ON audit_log (entity_type, entity_id);
CREATE INDEX idx_audit_timestamp ON audit_log (timestamp);
CREATE INDEX idx_audit_user      ON audit_log (user_email);
