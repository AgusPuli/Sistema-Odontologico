-- V6: Odontogram aggregate (odontogram + tooth_records + tooth_surface_conditions + tooth_history_entries)

CREATE TABLE odontograms (
    id              UUID         NOT NULL,
    tenant_id       UUID         NOT NULL,
    patient_id      UUID         NOT NULL,
    dentition       VARCHAR(20)  NOT NULL,
    general_notes   TEXT,
    is_current      BOOLEAN      NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT,

    CONSTRAINT pk_odontograms PRIMARY KEY (id),
    CONSTRAINT fk_odontograms_tenant  FOREIGN KEY (tenant_id)  REFERENCES tenants  (id),
    CONSTRAINT fk_odontograms_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT chk_odontograms_dentition CHECK (dentition IN ('PERMANENT', 'PRIMARY', 'MIXED'))
);

CREATE INDEX idx_odontograms_tenant         ON odontograms (tenant_id);
CREATE INDEX idx_odontograms_patient        ON odontograms (patient_id);
CREATE INDEX idx_odontograms_tenant_patient ON odontograms (tenant_id, patient_id);


CREATE TABLE tooth_records (
    id              UUID         NOT NULL,
    tenant_id       UUID         NOT NULL,
    odontogram_id   UUID         NOT NULL,
    fdi_number      INTEGER      NOT NULL,
    condition       VARCHAR(30)  NOT NULL,
    observation     TEXT,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT,

    CONSTRAINT pk_tooth_records PRIMARY KEY (id),
    CONSTRAINT fk_tooth_records_tenant     FOREIGN KEY (tenant_id)     REFERENCES tenants     (id),
    CONSTRAINT fk_tooth_records_odontogram FOREIGN KEY (odontogram_id) REFERENCES odontograms (id) ON DELETE CASCADE,
    CONSTRAINT uq_tooth_records_odontogram_fdi UNIQUE (odontogram_id, fdi_number)
);

CREATE INDEX idx_tooth_records_odontogram ON tooth_records (odontogram_id);
CREATE INDEX idx_tooth_records_tenant     ON tooth_records (tenant_id);


CREATE TABLE tooth_surface_conditions (
    id              UUID         NOT NULL,
    tenant_id       UUID         NOT NULL,
    tooth_record_id UUID         NOT NULL,
    surface         VARCHAR(5)   NOT NULL,
    condition       VARCHAR(30)  NOT NULL,
    notes           TEXT,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT,

    CONSTRAINT pk_tooth_surface_conditions PRIMARY KEY (id),
    CONSTRAINT fk_surface_tenant FOREIGN KEY (tenant_id)       REFERENCES tenants       (id),
    CONSTRAINT fk_surface_tooth  FOREIGN KEY (tooth_record_id) REFERENCES tooth_records (id) ON DELETE CASCADE,
    CONSTRAINT uq_surface_tooth_surface UNIQUE (tooth_record_id, surface),
    CONSTRAINT chk_surface_kind CHECK (surface IN ('M', 'D', 'V', 'L', 'O', 'I'))
);

CREATE INDEX idx_surface_tooth  ON tooth_surface_conditions (tooth_record_id);
CREATE INDEX idx_surface_tenant ON tooth_surface_conditions (tenant_id);


CREATE TABLE tooth_history_entries (
    id                  UUID         NOT NULL,
    tenant_id           UUID         NOT NULL,
    tooth_record_id     UUID         NOT NULL,
    finding             VARCHAR(30)  NOT NULL,
    surface             VARCHAR(5),
    treatment_status    VARCHAR(20)  NOT NULL,
    note                TEXT,
    recorded_by_email   VARCHAR(255),
    treatment_id        UUID,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    version             BIGINT,

    CONSTRAINT pk_tooth_history_entries PRIMARY KEY (id),
    CONSTRAINT fk_tooth_history_tenant    FOREIGN KEY (tenant_id)       REFERENCES tenants       (id),
    CONSTRAINT fk_tooth_history_tooth     FOREIGN KEY (tooth_record_id) REFERENCES tooth_records (id) ON DELETE CASCADE,
    CONSTRAINT fk_tooth_history_treatment FOREIGN KEY (treatment_id)    REFERENCES treatments    (id),
    CONSTRAINT chk_tooth_history_status CHECK (treatment_status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX idx_tooth_history_tooth  ON tooth_history_entries (tooth_record_id);
CREATE INDEX idx_tooth_history_tenant ON tooth_history_entries (tenant_id);
CREATE INDEX idx_tooth_history_status ON tooth_history_entries (treatment_status);
