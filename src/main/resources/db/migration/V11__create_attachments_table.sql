-- V11: Generic attachments — files associated with any owner entity.
-- Designed as a reusable module: today owners are PATIENT / CLINICAL_SESSION /
-- ESTIMATE / ODONTOGRAM, tomorrow we can attach to anything by adding the type
-- to the enum without a migration (no CHECK constraint on owner_type / category).
--
-- The actual bytes live in object storage (MinIO/S3). This table only tracks
-- metadata + the storage key so the file can be fetched / presigned later.

CREATE TABLE attachments (
    id              UUID            NOT NULL,
    tenant_id       UUID            NOT NULL,

    -- Polymorphic owner reference. We don't add a FK here on purpose: the owner
    -- type may be any aggregate and FKs would force us to ALTER on every new
    -- owner type. Integrity is enforced at the application layer.
    owner_type      VARCHAR(40)     NOT NULL,
    owner_id        UUID            NOT NULL,

    -- Optional secondary refs (a chest x-ray of tooth 36, photo of a session, etc.)
    tooth_fdi       INTEGER,
    session_id      UUID,

    category        VARCHAR(40)     NOT NULL,
    file_name       VARCHAR(255)    NOT NULL,
    storage_key     VARCHAR(500)    NOT NULL,
    content_type    VARCHAR(150),
    size_bytes      BIGINT,
    description     TEXT,
    taken_at        DATE,

    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT,

    CONSTRAINT pk_attachments PRIMARY KEY (id),
    CONSTRAINT fk_attachments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id)
);

CREATE INDEX idx_attachments_tenant ON attachments (tenant_id);
CREATE INDEX idx_attachments_owner  ON attachments (owner_type, owner_id);
CREATE INDEX idx_attachments_tenant_owner ON attachments (tenant_id, owner_type, owner_id);
CREATE INDEX idx_attachments_category ON attachments (category);
