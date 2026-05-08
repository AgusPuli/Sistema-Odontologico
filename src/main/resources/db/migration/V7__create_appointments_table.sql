-- V7: Appointments
CREATE TABLE appointments (
    id                  UUID         NOT NULL,
    tenant_id           UUID         NOT NULL,
    patient_id          UUID         NOT NULL,
    dentist_id          UUID,
    appointment_date    DATE         NOT NULL,
    appointment_time    TIME         NOT NULL,
    duration_minutes    INTEGER      NOT NULL,
    reason              VARCHAR(200),
    notes               TEXT,
    status              VARCHAR(20)  NOT NULL,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    version             BIGINT,

    CONSTRAINT pk_appointments PRIMARY KEY (id),
    CONSTRAINT fk_appointments_tenant  FOREIGN KEY (tenant_id)  REFERENCES tenants  (id),
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_appointments_dentist FOREIGN KEY (dentist_id) REFERENCES users    (id),
    CONSTRAINT chk_appointments_status CHECK (status IN
        ('SCHEDULED', 'CONFIRMED', 'CHECKED_IN', 'COMPLETED', 'CANCELLED', 'NO_SHOW', 'RESCHEDULED'))
);

CREATE INDEX idx_appointments_tenant             ON appointments (tenant_id);
CREATE INDEX idx_appointments_patient            ON appointments (patient_id);
CREATE INDEX idx_appointments_dentist            ON appointments (dentist_id);
CREATE INDEX idx_appointments_date               ON appointments (appointment_date);
CREATE INDEX idx_appointments_status             ON appointments (status);
CREATE INDEX idx_appointments_tenant_date        ON appointments (tenant_id, appointment_date);
CREATE INDEX idx_appointments_tenant_dentist_date ON appointments (tenant_id, dentist_id, appointment_date);
