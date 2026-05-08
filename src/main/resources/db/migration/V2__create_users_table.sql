-- V2: Create users table (Auth feature)
-- Extends BaseEntity: id, tenant_id, created_at, updated_at, created_by, updated_by, version
-- Email is unique per tenant (composite unique constraint).

CREATE TABLE users (
    id              UUID            NOT NULL,
    tenant_id       UUID            NOT NULL,
    email           VARCHAR(100)    NOT NULL,
    password_hash   TEXT            NOT NULL,
    first_name      VARCHAR(100)    NOT NULL,
    last_name       VARCHAR(100),
    role            VARCHAR(20)     NOT NULL,
    active          BOOLEAN         NOT NULL,
    specialty       VARCHAR(40),
    license_number  VARCHAR(50),
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email_tenant UNIQUE (email, tenant_id),
    CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    CONSTRAINT chk_users_role CHECK (role IN ('SUPERADMIN', 'ADMIN', 'MANAGER'))
);

CREATE INDEX idx_users_tenant_id ON users (tenant_id);
CREATE INDEX idx_users_email ON users (email);
