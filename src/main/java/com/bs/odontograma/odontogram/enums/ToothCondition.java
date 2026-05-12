package com.bs.odontograma.odontogram.enums;

/**
 * Diagnosis or condition recorded on a whole tooth.
 * Surface-level findings (e.g. caries on the M surface) live in {@link ToothSurfaceCondition}.
 *
 * Universal dental terminology — these labels are used the same way in Argentina
 * and the rest of Latin America. tooth_records.condition is stored as VARCHAR(30)
 * with no CHECK constraint, so adding values here is safe without a Flyway migration.
 */
public enum ToothCondition {
    // --- Restorative / surgical ---
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
    OBSERVATION,
    // --- Periodontal ---
    GINGIVITIS,
    CALCULUS,
    GINGIVAL_RECESSION,
    ABSCESS,
    // --- Anomalies / positioning ---
    FUSION,
    GEMINATION,
    ROTATION,        // giroversión
    MALPOSITION,
    DIASTEMA,
    IMPACTED,        // diente incluido / retenido
    // --- Function / wear ---
    MOBILITY,
    BRUXISM
}
