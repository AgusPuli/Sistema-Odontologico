-- V10: Clinical sessions (evolución por turno) — SOAP notes per visit.
-- A clinical session is the record of WHAT happened at a visit. Differs from
-- an appointment (which is just the calendar entry) and from an estimate
-- (which is the proposed budget).
--   appointment  → scheduled
--   estimate     → proposed money
--   session      → what was actually done / observed
-- Sessions can optionally link back to the originating appointment.
-- No CHECK on `status` so adding new states won't require a migration.

CREATE TABLE clinical_sessions (
    id                          UUID            NOT NULL,
    tenant_id                   UUID            NOT NULL,
    patient_id                  UUID            NOT NULL,
    appointment_id              UUID,
    dentist_id                  UUID            NOT NULL,
    session_date                TIMESTAMP       NOT NULL,
    duration_minutes            INTEGER,

    -- Vital signs at this session
    blood_pressure_systolic     INTEGER,
    blood_pressure_diastolic    INTEGER,
    heart_rate                  INTEGER,

    -- SOAP notes
    subjective                  TEXT,
    objective                   TEXT,
    assessment                  TEXT,
    plan                        TEXT,

    -- Anesthesia
    anesthesia_used             BOOLEAN         NOT NULL DEFAULT FALSE,
    anesthesia_type             VARCHAR(100),
    anesthesia_doses            INTEGER,

    -- Other
    materials_used              TEXT,
    general_notes               TEXT,
    next_appointment_recommendation TEXT,

    status                      VARCHAR(20)     NOT NULL,

    -- Audit (BaseEntity)
    created_at                  TIMESTAMP       NOT NULL,
    updated_at                  TIMESTAMP,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    version                     BIGINT,

    CONSTRAINT pk_clinical_sessions PRIMARY KEY (id),
    CONSTRAINT fk_clinical_sessions_tenant      FOREIGN KEY (tenant_id)      REFERENCES tenants      (id),
    CONSTRAINT fk_clinical_sessions_patient     FOREIGN KEY (patient_id)     REFERENCES patients     (id) ON DELETE CASCADE,
    CONSTRAINT fk_clinical_sessions_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id),
    CONSTRAINT fk_clinical_sessions_dentist     FOREIGN KEY (dentist_id)     REFERENCES users        (id)
);

CREATE INDEX idx_clinical_sessions_tenant   ON clinical_sessions (tenant_id);
CREATE INDEX idx_clinical_sessions_patient  ON clinical_sessions (patient_id);
CREATE INDEX idx_clinical_sessions_dentist  ON clinical_sessions (dentist_id);
CREATE INDEX idx_clinical_sessions_date     ON clinical_sessions (session_date);


-- Procedures performed during a session (a session can include multiple).
-- Mirrors estimate_items but represents what was DONE, not what was proposed.
CREATE TABLE clinical_session_procedures (
    id              UUID            NOT NULL,
    tenant_id       UUID            NOT NULL,
    session_id      UUID            NOT NULL,
    treatment_id    UUID            NOT NULL,
    fdi_number      INTEGER,
    notes           TEXT,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT,

    CONSTRAINT pk_clinical_session_procedures PRIMARY KEY (id),
    CONSTRAINT fk_csp_tenant    FOREIGN KEY (tenant_id)    REFERENCES tenants            (id),
    CONSTRAINT fk_csp_session   FOREIGN KEY (session_id)   REFERENCES clinical_sessions  (id) ON DELETE CASCADE,
    CONSTRAINT fk_csp_treatment FOREIGN KEY (treatment_id) REFERENCES treatments         (id)
);

CREATE INDEX idx_csp_session   ON clinical_session_procedures (session_id);
CREATE INDEX idx_csp_treatment ON clinical_session_procedures (treatment_id);
