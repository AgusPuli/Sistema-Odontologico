-- V9: Patient structured medical history (anamnesis).
-- 1:1 with patient. The patient row stays simple — this table holds the full
-- clinical questionnaire so it can grow without bloating `patients`.
-- No CHECK on enum columns (asa_classification, blood_type, flossing_frequency) so
-- adding new values does NOT require a migration. See docs/ARCHITECTURE.md.

CREATE TABLE medical_histories (
    id                          UUID            NOT NULL,
    tenant_id                   UUID            NOT NULL,
    patient_id                  UUID            NOT NULL,

    -- General risk / blood
    asa_classification          VARCHAR(10),
    blood_type                  VARCHAR(15),

    -- Chronic conditions (boolean flags + optional per-condition notes)
    diabetes                    BOOLEAN         NOT NULL DEFAULT FALSE,
    diabetes_notes              TEXT,
    hypertension                BOOLEAN         NOT NULL DEFAULT FALSE,
    hypertension_notes          TEXT,
    heart_disease               BOOLEAN         NOT NULL DEFAULT FALSE,
    heart_disease_notes         TEXT,
    kidney_disease              BOOLEAN         NOT NULL DEFAULT FALSE,
    liver_disease               BOOLEAN         NOT NULL DEFAULT FALSE,
    respiratory_disease         BOOLEAN         NOT NULL DEFAULT FALSE,
    thyroid_disease             BOOLEAN         NOT NULL DEFAULT FALSE,
    cancer                      BOOLEAN         NOT NULL DEFAULT FALSE,
    bleeding_disorder           BOOLEAN         NOT NULL DEFAULT FALSE,
    anticoagulant_use           BOOLEAN         NOT NULL DEFAULT FALSE,
    epilepsy                    BOOLEAN         NOT NULL DEFAULT FALSE,
    psychiatric_condition       BOOLEAN         NOT NULL DEFAULT FALSE,
    other_conditions            TEXT,

    -- Allergies
    allergy_penicillin          BOOLEAN         NOT NULL DEFAULT FALSE,
    allergy_latex               BOOLEAN         NOT NULL DEFAULT FALSE,
    allergy_anesthesia          BOOLEAN         NOT NULL DEFAULT FALSE,
    allergy_other               TEXT,

    -- Current medications (free-form so we don't lock ourselves to a structure)
    current_medications         TEXT,

    -- Habits
    smoker                      BOOLEAN         NOT NULL DEFAULT FALSE,
    smoking_details             TEXT,
    alcohol                     BOOLEAN         NOT NULL DEFAULT FALSE,
    alcohol_details             TEXT,
    bruxism                     BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Female-specific
    pregnant                    BOOLEAN         NOT NULL DEFAULT FALSE,
    pregnancy_weeks             INTEGER,
    breastfeeding               BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Dental history & habits
    chief_complaint             TEXT,
    last_dental_visit           DATE,
    previous_dental_problems    TEXT,
    brushing_per_day            INTEGER,
    flossing_frequency          VARCHAR(20),

    -- Vital signs (snapshot at last update)
    blood_pressure_systolic     INTEGER,
    blood_pressure_diastolic    INTEGER,
    heart_rate                  INTEGER,

    -- Free-form catch-all + review tracking
    general_observations        TEXT,
    last_reviewed_at            TIMESTAMP,

    -- Audit (mirrors BaseEntity)
    created_at                  TIMESTAMP       NOT NULL,
    updated_at                  TIMESTAMP,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    version                     BIGINT,

    CONSTRAINT pk_medical_histories PRIMARY KEY (id),
    CONSTRAINT fk_medical_histories_tenant  FOREIGN KEY (tenant_id)  REFERENCES tenants  (id),
    CONSTRAINT fk_medical_histories_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    -- One medical history per patient (per tenant)
    CONSTRAINT uq_medical_histories_patient UNIQUE (tenant_id, patient_id)
);

CREATE INDEX idx_medical_histories_tenant  ON medical_histories (tenant_id);
CREATE INDEX idx_medical_histories_patient ON medical_histories (patient_id);
