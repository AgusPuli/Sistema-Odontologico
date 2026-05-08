-- V5: Treatment catalog (per-tenant)
CREATE TABLE treatments (
    id                UUID            NOT NULL,
    tenant_id         UUID            NOT NULL,
    code              VARCHAR(30)     NOT NULL,
    name              VARCHAR(200)    NOT NULL,
    specialty         VARCHAR(40)     NOT NULL,
    default_price     NUMERIC(14, 2),
    duration_minutes  INTEGER,
    description       TEXT,
    active            BOOLEAN         NOT NULL,
    created_at        TIMESTAMP       NOT NULL,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    version           BIGINT,

    CONSTRAINT pk_treatments PRIMARY KEY (id),
    CONSTRAINT fk_treatments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    CONSTRAINT uq_treatments_tenant_code UNIQUE (tenant_id, code)
);

CREATE INDEX idx_treatments_tenant            ON treatments (tenant_id);
CREATE INDEX idx_treatments_specialty         ON treatments (specialty);
CREATE INDEX idx_treatments_tenant_specialty  ON treatments (tenant_id, specialty);
