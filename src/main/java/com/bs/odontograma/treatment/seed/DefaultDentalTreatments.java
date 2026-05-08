package com.bs.odontograma.treatment.seed;

import com.bs.odontograma.shared.enums.DentalSpecialty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Default catalog of common dental treatments by specialty, used to bootstrap a new clinic.
 *
 * Prices are placeholders (BigDecimal.ZERO by default) — every clinic should adjust them
 * after seeding via the catalog UI. Codes follow the {@code SPECIALTY-XX} convention.
 *
 * To extend the catalog: add new {@link Item} entries here AND publish a Flyway migration
 * if you want the change to land in already-seeded clinics.
 */
public final class DefaultDentalTreatments {

    private DefaultDentalTreatments() {}

    public record Item(
            String code,
            String name,
            DentalSpecialty specialty,
            Integer durationMinutes,
            String description
    ) {
        public BigDecimal defaultPrice() { return BigDecimal.ZERO; }
    }

    public static final List<Item> CATALOG = List.of(
            // ============ Diagnóstico ============
            new Item("DX-INIT",   "Consulta inicial / Examen clínico",          DentalSpecialty.GENERAL_DENTISTRY,    30, "Primera consulta y diagnóstico general"),
            new Item("DX-CTRL",   "Consulta de control",                        DentalSpecialty.GENERAL_DENTISTRY,    20, "Consulta de seguimiento"),
            new Item("DX-RXP",    "Radiografía periapical",                     DentalSpecialty.DIAGNOSTIC_IMAGING,   10, "Rx periapical de una pieza"),
            new Item("DX-RXPAN",  "Radiografía panorámica",                     DentalSpecialty.DIAGNOSTIC_IMAGING,   15, "Ortopantomografía"),
            new Item("DX-CBCT",   "Tomografía cone-beam",                       DentalSpecialty.DIAGNOSTIC_IMAGING,   30, "TC para implantes / endodoncia"),
            new Item("DX-MOD",    "Modelos de estudio",                         DentalSpecialty.DIAGNOSTIC_IMAGING,   30, "Toma de impresiones para modelos"),

            // ============ Operatoria / Odontología General ============
            new Item("OP-PROFI",  "Profilaxis / Limpieza dental",               DentalSpecialty.OPERATIVE_DENTISTRY,  45, "Limpieza y pulido"),
            new Item("OP-DETART", "Detartraje supra-gingival",                  DentalSpecialty.OPERATIVE_DENTISTRY,  60, "Eliminación de cálculo dental"),
            new Item("OP-FLUOR",  "Aplicación tópica de flúor",                 DentalSpecialty.OPERATIVE_DENTISTRY,  20, "Flúor barnizado o gel"),
            new Item("OP-SELL",   "Sellante de fosas y fisuras",                DentalSpecialty.OPERATIVE_DENTISTRY,  20, "Sellado preventivo de molares"),
            new Item("OP-RES1",   "Obturación con resina - 1 superficie",       DentalSpecialty.OPERATIVE_DENTISTRY,  30, null),
            new Item("OP-RES2",   "Obturación con resina - 2 superficies",      DentalSpecialty.OPERATIVE_DENTISTRY,  45, null),
            new Item("OP-RES3",   "Obturación con resina - 3 o más superficies",DentalSpecialty.OPERATIVE_DENTISTRY,  60, null),
            new Item("OP-AMG",    "Obturación con amalgama",                    DentalSpecialty.OPERATIVE_DENTISTRY,  45, null),
            new Item("OP-RECON",  "Reconstrucción dental",                      DentalSpecialty.OPERATIVE_DENTISTRY,  60, "Pieza con destrucción amplia"),
            new Item("OP-BLAN-C", "Blanqueamiento en consultorio",              DentalSpecialty.AESTHETIC_DENTISTRY,  90, null),
            new Item("OP-BLAN-D", "Blanqueamiento domiciliario",                DentalSpecialty.AESTHETIC_DENTISTRY,  30, "Cubetas + gel"),

            // ============ Endodoncia ============
            new Item("ENDO-1R",   "Tratamiento de conducto - unirradicular",    DentalSpecialty.ENDODONTICS,          60, "Incisivos y caninos"),
            new Item("ENDO-2R",   "Tratamiento de conducto - birradicular",     DentalSpecialty.ENDODONTICS,          90, "Premolares"),
            new Item("ENDO-3R",   "Tratamiento de conducto - multirradicular",  DentalSpecialty.ENDODONTICS,         120, "Molares"),
            new Item("ENDO-RET",  "Retratamiento endodóntico",                  DentalSpecialty.ENDODONTICS,         120, null),
            new Item("ENDO-APIC", "Apicectomía",                                DentalSpecialty.ENDODONTICS,          90, "Cirugía apical"),
            new Item("ENDO-PUL",  "Pulpotomía",                                 DentalSpecialty.ENDODONTICS,          45, null),

            // ============ Periodoncia ============
            new Item("PERI-RAR",  "Raspaje y alisado radicular - cuadrante",    DentalSpecialty.PERIODONTICS,         60, null),
            new Item("PERI-CUR",  "Curetaje gingival",                          DentalSpecialty.PERIODONTICS,         45, null),
            new Item("PERI-CIRG", "Cirugía periodontal a colgajo",              DentalSpecialty.PERIODONTICS,         90, null),
            new Item("PERI-GING", "Gingivectomía / Gingivoplastia",             DentalSpecialty.PERIODONTICS,         60, null),
            new Item("PERI-INJ",  "Injerto gingival libre",                     DentalSpecialty.PERIODONTICS,         90, null),

            // ============ Cirugía Oral ============
            new Item("CIR-EXT",   "Extracción simple",                          DentalSpecialty.ORAL_SURGERY,         30, null),
            new Item("CIR-EXTQ",  "Extracción quirúrgica",                      DentalSpecialty.ORAL_SURGERY,         60, "Pieza retenida"),
            new Item("CIR-COR-NI","Extracción cordal - no incluido",            DentalSpecialty.ORAL_SURGERY,         45, "Muela del juicio erupcionada"),
            new Item("CIR-COR-I", "Extracción cordal - incluido",               DentalSpecialty.ORAL_SURGERY,         90, "Muela del juicio retenida"),
            new Item("CIR-FREN",  "Frenectomía",                                DentalSpecialty.ORAL_SURGERY,         45, null),
            new Item("CIR-ALV",   "Alveoloplastia",                             DentalSpecialty.ORAL_SURGERY,         60, null),
            new Item("CIR-BIO",   "Biopsia de tejido oral",                     DentalSpecialty.ORAL_SURGERY,         45, null),

            // ============ Ortodoncia ============
            new Item("ORTO-DX",   "Diagnóstico y plan ortodóntico",             DentalSpecialty.ORTHODONTICS,         60, "Modelos, fotos, cefalometría"),
            new Item("ORTO-BMET", "Brackets metálicos - instalación",           DentalSpecialty.ORTHODONTICS,         90, "Por arcada"),
            new Item("ORTO-BEST", "Brackets estéticos - instalación",           DentalSpecialty.ORTHODONTICS,         90, "Cerámicos / zafiro"),
            new Item("ORTO-BAUT", "Brackets autoligado - instalación",          DentalSpecialty.ORTHODONTICS,         90, null),
            new Item("ORTO-ALIN", "Alineadores invisibles - tratamiento",       DentalSpecialty.ORTHODONTICS,         60, "Tipo Invisalign"),
            new Item("ORTO-CTRL", "Control de ortodoncia",                      DentalSpecialty.ORTHODONTICS,         30, "Activación mensual"),
            new Item("ORTO-RET",  "Retención post-tratamiento",                 DentalSpecialty.ORTHODONTICS,         45, "Placa o retenedor fijo"),
            new Item("ORTO-EXP",  "Expansor maxilar",                           DentalSpecialty.ORTHODONTICS,         45, null),

            // ============ Odontopediatría ============
            new Item("OPED-CONS", "Consulta odontopediátrica",                  DentalSpecialty.PEDIATRIC_DENTISTRY,  30, null),
            new Item("OPED-PROFI","Profilaxis pediátrica",                      DentalSpecialty.PEDIATRIC_DENTISTRY,  30, null),
            new Item("OPED-FLUOR","Flúor pediátrico",                           DentalSpecialty.PEDIATRIC_DENTISTRY,  20, null),
            new Item("OPED-SELL", "Sellante pediátrico",                        DentalSpecialty.PEDIATRIC_DENTISTRY,  30, null),
            new Item("OPED-PUL",  "Pulpotomía pediátrica",                      DentalSpecialty.PEDIATRIC_DENTISTRY,  45, null),
            new Item("OPED-COR",  "Corona pediátrica de acero / zirconio",      DentalSpecialty.PEDIATRIC_DENTISTRY,  45, null),
            new Item("OPED-EXTT", "Extracción de pieza temporal",               DentalSpecialty.PEDIATRIC_DENTISTRY,  20, null),
            new Item("OPED-MANT", "Mantenedor de espacio",                      DentalSpecialty.PEDIATRIC_DENTISTRY,  60, null),

            // ============ Implantología ============
            new Item("IMP-EVAL",  "Evaluación pre-implante",                    DentalSpecialty.IMPLANTOLOGY,         45, null),
            new Item("IMP-COL",   "Colocación de implante dental",              DentalSpecialty.IMPLANTOLOGY,        120, null),
            new Item("IMP-COR",   "Corona sobre implante",                      DentalSpecialty.IMPLANTOLOGY,         90, null),
            new Item("IMP-OSEO",  "Regeneración ósea / Injerto óseo",           DentalSpecialty.IMPLANTOLOGY,         90, null),
            new Item("IMP-SENO",  "Elevación de seno maxilar",                  DentalSpecialty.IMPLANTOLOGY,        120, null),

            // ============ Prótesis / Rehabilitación ============
            new Item("PRO-COR",   "Corona unitaria (porcelana / zirconio)",     DentalSpecialty.PROSTHODONTICS,       90, null),
            new Item("PRO-PTE",   "Puente fijo - 3 unidades",                   DentalSpecialty.PROSTHODONTICS,      120, null),
            new Item("PRO-INC",   "Incrustación / Inlay-Onlay",                 DentalSpecialty.PROSTHODONTICS,       90, null),
            new Item("PRO-RPP",   "Prótesis removible parcial",                 DentalSpecialty.PROSTHODONTICS,       90, null),
            new Item("PRO-RPT",   "Prótesis removible total",                   DentalSpecialty.PROSTHODONTICS,       90, "Dentadura completa"),
            new Item("PRO-REP",   "Reparación de prótesis",                     DentalSpecialty.PROSTHODONTICS,       45, null),
            new Item("PRO-REB",   "Rebase de prótesis",                         DentalSpecialty.PROSTHODONTICS,       45, null),

            // ============ Estética ============
            new Item("EST-CARR",  "Carilla de resina directa",                  DentalSpecialty.AESTHETIC_DENTISTRY,  60, null),
            new Item("EST-CARP",  "Carilla de porcelana",                       DentalSpecialty.AESTHETIC_DENTISTRY,  90, null),
            new Item("EST-DSD",   "Diseño de sonrisa (DSD)",                    DentalSpecialty.AESTHETIC_DENTISTRY,  60, "Planificación digital de la sonrisa")
    );
}
