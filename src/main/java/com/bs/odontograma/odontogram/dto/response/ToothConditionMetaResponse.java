package com.bs.odontograma.odontogram.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Catalog-style DTO exposing each {@link com.bs.odontograma.odontogram.enums.ToothCondition}
 * with its display label and category. Surfaced at GET /api/odontogram/conditions so the
 * frontend can render menus / palettes without hard-coding the enum locally.
 *
 * Visual concerns (color, symbol, palette order) intentionally stay in the frontend
 * config — they are presentation-layer details. The backend only ships the source of
 * truth for which conditions exist and how to label them.
 */
@Data
@Builder
@AllArgsConstructor
public class ToothConditionMetaResponse {
    /** Enum literal — also the API value used on writes. */
    private String key;
    /** Spanish display label. */
    private String label;
    /** restorative | periodontal | anomaly | function */
    private String category;
}
