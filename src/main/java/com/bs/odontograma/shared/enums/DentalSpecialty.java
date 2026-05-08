package com.bs.odontograma.shared.enums;

import lombok.Getter;

/**
 * Dental specialties recognized by the system.
 *
 * Used both to tag a User (the dentist's area of expertise) and to classify
 * Treatments in the catalog so the UI can group/filter by specialty.
 */
@Getter
public enum DentalSpecialty {
    GENERAL_DENTISTRY("Odontología General"),
    OPERATIVE_DENTISTRY("Operatoria Dental"),
    ENDODONTICS("Endodoncia"),
    PERIODONTICS("Periodoncia"),
    ORAL_SURGERY("Cirugía Oral y Maxilofacial"),
    ORTHODONTICS("Ortodoncia"),
    PEDIATRIC_DENTISTRY("Odontopediatría"),
    PROSTHODONTICS("Prótesis y Rehabilitación Oral"),
    IMPLANTOLOGY("Implantología"),
    AESTHETIC_DENTISTRY("Estética Dental"),
    DIAGNOSTIC_IMAGING("Diagnóstico por Imágenes");

    private final String displayName;

    DentalSpecialty(String displayName) {
        this.displayName = displayName;
    }
}
