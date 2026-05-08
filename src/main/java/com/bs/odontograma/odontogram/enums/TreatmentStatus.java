package com.bs.odontograma.odontogram.enums;

/**
 * Status of a tooth-level treatment item in the patient's "Plan de tratamiento".
 *  - PENDING     = Detected/diagnosed but not started
 *  - IN_PROGRESS = Treatment started, not yet finished
 *  - COMPLETED   = Treatment finished
 *  - CANCELLED   = Plan was cancelled (patient declined, etc.)
 */
public enum TreatmentStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
