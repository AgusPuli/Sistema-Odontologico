package com.bs.odontograma.attachment.enums;

/**
 * Clinical category of an attachment. Drives how the UI labels and groups it.
 * No DB CHECK constraint — extensible without migration.
 */
public enum AttachmentCategory {
    // ---- Imaging ----
    XRAY_PANORAMIC,       // ortopantomografía
    XRAY_PERIAPICAL,
    XRAY_BITEWING,        // aleta de mordida
    XRAY_OCCLUSAL,
    XRAY_CBCT,            // tomografía cone-beam
    PHOTO_INTRAORAL,
    PHOTO_EXTRAORAL,
    PHOTO_SMILE,
    PHOTO_FACE,
    // ---- Documents ----
    CONSENT_FORM,         // consentimiento informado
    PRESCRIPTION,         // receta
    LAB_ORDER,            // pedido a laboratorio
    REFERRAL,             // derivación
    INSURANCE_DOCUMENT,
    REPORT,               // informe médico
    OTHER
}
