-- V4: Patients table
CREATE TABLE patients (
    id                UUID            NOT NULL,
    tenant_id         UUID            NOT NULL,
    first_name        VARCHAR(100)    NOT NULL,
    last_name         VARCHAR(100)    NOT NULL,
    document_number   VARCHAR(30),
    birth_date        DATE,
    gender            VARCHAR(20),
    phone             VARCHAR(30),
    email             VARCHAR(100),
    address           VARCHAR(200),
    health_insurance  VARCHAR(100),
    insurance_number  VARCHAR(50),
    medical_notes     TEXT,
    allergies         TEXT,
    active            BOOLEAN         NOT NULL,
    created_at        TIMESTAMP       NOT NULL,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    version           BIGINT,

    CONSTRAINT pk_patients PRIMARY KEY (id),
    CONSTRAINT fk_patients_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    CONSTRAINT uq_patients_tenant_document UNIQUE (tenant_id, document_number),
    CONSTRAINT chk_patients_gender CHECK (gender IS NULL OR gender IN ('MALE', 'FEMALE', 'OTHER'))
);

CREATE INDEX idx_patients_tenant     ON patients (tenant_id);
CREATE INDEX idx_patients_document   ON patients (document_number);
CREATE INDEX idx_patients_last_name  ON patients (last_name);
