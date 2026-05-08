-- V8: Estimates (presupuestos) and items
CREATE TABLE estimates (
    id              UUID            NOT NULL,
    tenant_id       UUID            NOT NULL,
    patient_id      UUID            NOT NULL,
    issue_date      DATE            NOT NULL,
    valid_until     DATE,
    status          VARCHAR(20)     NOT NULL,
    total           NUMERIC(14, 2)  NOT NULL,
    notes           TEXT,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT,

    CONSTRAINT pk_estimates PRIMARY KEY (id),
    CONSTRAINT fk_estimates_tenant  FOREIGN KEY (tenant_id)  REFERENCES tenants  (id),
    CONSTRAINT fk_estimates_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT chk_estimates_status CHECK (status IN
        ('DRAFT', 'SENT', 'ACCEPTED', 'REJECTED', 'EXPIRED', 'CANCELLED'))
);

CREATE INDEX idx_estimates_tenant  ON estimates (tenant_id);
CREATE INDEX idx_estimates_patient ON estimates (patient_id);
CREATE INDEX idx_estimates_status  ON estimates (status);


CREATE TABLE estimate_items (
    id              UUID            NOT NULL,
    tenant_id       UUID            NOT NULL,
    estimate_id     UUID            NOT NULL,
    treatment_id    UUID            NOT NULL,
    fdi_number      INTEGER,
    quantity        INTEGER         NOT NULL,
    unit_price      NUMERIC(14, 2)  NOT NULL,
    subtotal        NUMERIC(14, 2)  NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT,

    CONSTRAINT pk_estimate_items PRIMARY KEY (id),
    CONSTRAINT fk_estimate_items_tenant    FOREIGN KEY (tenant_id)    REFERENCES tenants    (id),
    CONSTRAINT fk_estimate_items_estimate  FOREIGN KEY (estimate_id)  REFERENCES estimates  (id) ON DELETE CASCADE,
    CONSTRAINT fk_estimate_items_treatment FOREIGN KEY (treatment_id) REFERENCES treatments (id)
);

CREATE INDEX idx_estimate_items_estimate  ON estimate_items (estimate_id);
CREATE INDEX idx_estimate_items_treatment ON estimate_items (treatment_id);
