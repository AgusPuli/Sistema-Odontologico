package com.bs.odontograma.clinicalsession.enums;

/**
 * Status of a clinical session record.
 * DRAFT     — being filled by the dentist (allows further edits)
 * COMPLETED — locked / signed off
 * CANCELLED — session never happened or invalidated
 *
 * The transition from DRAFT to COMPLETED should ideally lock further edits,
 * but the MVP allows reopening for corrections. We can add a CHECK constraint
 * later if needed.
 */
public enum ClinicalSessionStatus {
    DRAFT,
    COMPLETED,
    CANCELLED
}
