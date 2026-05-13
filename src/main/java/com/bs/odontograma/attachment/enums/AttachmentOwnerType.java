package com.bs.odontograma.attachment.enums;

/**
 * Polymorphic owner of an attachment. Extend as needed — no DB CHECK constraint,
 * so adding values does not require a Flyway migration.
 */
public enum AttachmentOwnerType {
    PATIENT,
    CLINICAL_SESSION,
    ESTIMATE,
    ODONTOGRAM,
    APPOINTMENT
}
