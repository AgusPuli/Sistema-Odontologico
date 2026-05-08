package com.bs.odontograma.odontogram.enums;

/**
 * Diagnosis or condition recorded on a whole tooth.
 * Surface-level findings (e.g. caries on the M surface) live in {@link ToothSurfaceCondition}.
 *
 * The list is intentionally narrow for the MVP. Add new entries (and a Flyway migration to
 * extend the CHECK constraint) when the dental workflow needs them.
 */
public enum ToothCondition {
    HEALTHY,
    CARIES,
    EXTRACTED,
    RESTORATION,
    ENDODONTICS,
    IMPLANT,
    CROWN,
    MISSING,
    PROSTHESIS,
    FRACTURE,
    SEALANT,
    OBSERVATION
}
