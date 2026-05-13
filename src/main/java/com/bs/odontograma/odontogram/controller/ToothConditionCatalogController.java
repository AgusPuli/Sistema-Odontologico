package com.bs.odontograma.odontogram.controller;

import com.bs.odontograma.odontogram.dto.response.ToothConditionMetaResponse;
import com.bs.odontograma.odontogram.enums.ToothCondition;
import com.bs.odontograma.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only catalog endpoint that exposes the set of supported ToothCondition values
 * along with their Spanish label and category. The frontend can call this once on
 * boot and render menus dynamically — so adding a new finding only requires updating
 * this enum on the backend and the {@code config/conditions.ts} file on the front.
 *
 * Why server-driven: the day a clinic asks for a custom finding ("Anomalía-X"), we
 * can move this metadata into the database (a {@code tooth_condition_catalog}
 * table) without changing the API contract.
 */
@RestController
@RequestMapping("/api/odontogram/conditions")
@Tag(name = "Odontogram catalog", description = "Static catalogs used by the dental chart UI")
public class ToothConditionCatalogController {

    @GetMapping
    @Operation(summary = "List all supported tooth conditions with their labels and categories")
    public ResponseEntity<ApiResponse<List<ToothConditionMetaResponse>>> list() {
        List<ToothConditionMetaResponse> response = List.of(
                // ---- Restorative / surgical ----
                meta(ToothCondition.HEALTHY,            "Sano",                  "restorative"),
                meta(ToothCondition.CARIES,             "Caries",                "restorative"),
                meta(ToothCondition.RESTORATION,        "Restauración",          "restorative"),
                meta(ToothCondition.ENDODONTICS,        "Endodoncia",            "restorative"),
                meta(ToothCondition.CROWN,              "Corona",                "restorative"),
                meta(ToothCondition.EXTRACTED,          "Extraído",              "restorative"),
                meta(ToothCondition.MISSING,            "Ausente",               "restorative"),
                meta(ToothCondition.IMPLANT,            "Implante",              "restorative"),
                meta(ToothCondition.PROSTHESIS,         "Prótesis",              "restorative"),
                meta(ToothCondition.FRACTURE,           "Fractura",              "restorative"),
                meta(ToothCondition.SEALANT,            "Sellante",              "restorative"),
                // ---- Periodontal ----
                meta(ToothCondition.GINGIVITIS,         "Gingivitis",            "periodontal"),
                meta(ToothCondition.CALCULUS,           "Cálculo / Sarro",       "periodontal"),
                meta(ToothCondition.GINGIVAL_RECESSION, "Recesión gingival",     "periodontal"),
                meta(ToothCondition.ABSCESS,            "Absceso",               "periodontal"),
                // ---- Anomalies / positioning ----
                meta(ToothCondition.ROTATION,           "Giroversión",           "anomaly"),
                meta(ToothCondition.MALPOSITION,        "Malposición",           "anomaly"),
                meta(ToothCondition.DIASTEMA,           "Diastema",              "anomaly"),
                meta(ToothCondition.FUSION,             "Fusión",                "anomaly"),
                meta(ToothCondition.GEMINATION,         "Geminación",            "anomaly"),
                meta(ToothCondition.IMPACTED,           "Incluido / Retenido",   "anomaly"),
                // ---- Function / wear ----
                meta(ToothCondition.MOBILITY,           "Movilidad",             "function"),
                meta(ToothCondition.BRUXISM,            "Bruxismo / Desgaste",   "function"),
                meta(ToothCondition.OBSERVATION,        "Observación",           "function")
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private static ToothConditionMetaResponse meta(ToothCondition c, String label, String category) {
        return ToothConditionMetaResponse.builder()
                .key(c.name())
                .label(label)
                .category(category)
                .build();
    }
}
