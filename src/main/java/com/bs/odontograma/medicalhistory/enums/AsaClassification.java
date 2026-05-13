package com.bs.odontograma.medicalhistory.enums;

/**
 * American Society of Anesthesiologists physical status classification.
 * Used by dentists to assess anesthetic risk before treatment.
 *
 * I   – Healthy patient.
 * II  – Mild systemic disease (controlled HTN, mild diabetes).
 * III – Severe systemic disease (poorly controlled diabetes, recent MI).
 * IV  – Severe disease that is a constant threat to life.
 * V   – Moribund patient — only the most invasive treatments here.
 */
public enum AsaClassification {
    ASA_I,
    ASA_II,
    ASA_III,
    ASA_IV,
    ASA_V
}
