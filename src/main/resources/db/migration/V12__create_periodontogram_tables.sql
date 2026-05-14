-- V12: Periodontogram aggregate
--   periodontograms          → exam (1 current per patient, history versions kept)
--   periodontogram_teeth     → one row per tooth measured (mobility, furcation)
--   periodontogram_sites     → six rows per tooth (PD, recession, BoP, plaque per site)
--
-- Probing convention: 6 sites per tooth as recommended by AAP / FDI:
--   MV  = mesiovestibular
--   V   = vestibular (mid-buccal)
--   DV  = distovestibular
--   ML  = mesiolingual
--   L   = lingual (mid-lingual / palatal)
--   DL  = distolingual
--
-- No CHECK constraints on enum columns so adding values stays migration-free.

CREATE TABLE periodontograms (
    id              UUID            NOT NULL,
    tenant_id       UUID            NOT NULL,
    patient_id      UUID            NOT NULL,
    exam_date       DATE            NOT NULL,
    current         BOOLEAN         NOT NULL DEFAULT TRUE,
    general_notes   TEXT,
    -- Aggregate scores recomputed on save
    bleeding_index  NUMERIC(5, 2),  -- % of sites with BoP (0-100)
    plaque_index    NUMERIC(5, 2),  -- % of sites with plaque (0-100)

    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT,

    CONSTRAINT pk_periodontograms PRIMARY KEY (id),
    CONSTRAINT fk_perio_tenant  FOREIGN KEY (tenant_id)  REFERENCES tenants  (id),
    CONSTRAINT fk_perio_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE
);

CREATE INDEX idx_perio_tenant ON periodontograms (tenant_id);
CREATE INDEX idx_perio_patient ON periodontograms (patient_id);
CREATE INDEX idx_perio_current ON periodontograms (tenant_id, patient_id, current);


CREATE TABLE periodontogram_teeth (
    id                  UUID            NOT NULL,
    tenant_id           UUID            NOT NULL,
    periodontogram_id   UUID            NOT NULL,
    fdi_number          INTEGER         NOT NULL,
    mobility            INTEGER,        -- 0..3 (Miller classification)
    furcation           INTEGER,        -- 0..3 (Glickman) — null for non-molars
    notes               TEXT,

    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    version             BIGINT,

    CONSTRAINT pk_periodontogram_teeth PRIMARY KEY (id),
    CONSTRAINT fk_perio_tooth_tenant  FOREIGN KEY (tenant_id)         REFERENCES tenants         (id),
    CONSTRAINT fk_perio_tooth_parent  FOREIGN KEY (periodontogram_id) REFERENCES periodontograms (id) ON DELETE CASCADE,
    CONSTRAINT uq_perio_tooth_fdi UNIQUE (periodontogram_id, fdi_number)
);

CREATE INDEX idx_perio_tooth_parent ON periodontogram_teeth (periodontogram_id);
CREATE INDEX idx_perio_tooth_tenant ON periodontogram_teeth (tenant_id);


CREATE TABLE periodontogram_sites (
    id              UUID            NOT NULL,
    tenant_id       UUID            NOT NULL,
    tooth_id        UUID            NOT NULL,
    site            VARCHAR(3)      NOT NULL,   -- MV / V / DV / ML / L / DL
    probing_depth   INTEGER,                    -- mm (null = not measured)
    recession       INTEGER,                    -- mm (positive = recession, negative = overgrowth)
    bleeding        BOOLEAN         NOT NULL DEFAULT FALSE,
    suppuration     BOOLEAN         NOT NULL DEFAULT FALSE,
    plaque          BOOLEAN         NOT NULL DEFAULT FALSE,

    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT,

    CONSTRAINT pk_periodontogram_sites PRIMARY KEY (id),
    CONSTRAINT fk_perio_site_tenant FOREIGN KEY (tenant_id) REFERENCES tenants              (id),
    CONSTRAINT fk_perio_site_tooth  FOREIGN KEY (tooth_id)  REFERENCES periodontogram_teeth (id) ON DELETE CASCADE,
    CONSTRAINT uq_perio_site UNIQUE (tooth_id, site)
);

CREATE INDEX idx_perio_site_tooth  ON periodontogram_sites (tooth_id);
CREATE INDEX idx_perio_site_tenant ON periodontogram_sites (tenant_id);
