package com.bs.odontograma.medicalhistory.enums;

/**
 * ABO + Rh blood typing.
 * Stored as VARCHAR(15) with no CHECK constraint so we can add UNKNOWN-like
 * values later without a migration.
 */
public enum BloodType {
    A_POSITIVE,
    A_NEGATIVE,
    B_POSITIVE,
    B_NEGATIVE,
    AB_POSITIVE,
    AB_NEGATIVE,
    O_POSITIVE,
    O_NEGATIVE,
    UNKNOWN
}
