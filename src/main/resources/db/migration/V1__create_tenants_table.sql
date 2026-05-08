-- V1: Create tenants table
-- Tenant is the root multi-tenancy entity. No foreign keys to other tables.

CREATE TABLE tenants (
    id              UUID            NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    legal_name      VARCHAR(200),
    tax_id          VARCHAR(20),
    address         VARCHAR(200),
    email           VARCHAR(100),
    phone           VARCHAR(20),
    logo_url        VARCHAR(500),
    iva_condition   VARCHAR(100),
    iibb            VARCHAR(50),
    activity_start_date DATE,
    subscription_plan   VARCHAR(20)     NOT NULL,
    max_users       INTEGER         NOT NULL,
    active          BOOLEAN         NOT NULL,
    plan_expiration TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    version         BIGINT,

    CONSTRAINT pk_tenants PRIMARY KEY (id),
    CONSTRAINT chk_tenants_subscription_plan CHECK (subscription_plan IN ('BASIC', 'PROFESSIONAL', 'ENTERPRISE', 'SUPER'))
);
