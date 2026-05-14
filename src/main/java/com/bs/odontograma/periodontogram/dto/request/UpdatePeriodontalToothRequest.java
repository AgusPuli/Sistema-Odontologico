package com.bs.odontograma.periodontogram.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

/**
 * Patch payload for one tooth in a periodontogram. The frontend submits this on
 * each tooth save: tooth-level values + the 6 site measurements.
 *
 * Null fields are kept as-is. {@code sites} replaces the previous list when
 * provided (matches the UI behavior — user edits all 6 sites at once).
 */
@Data
public class UpdatePeriodontalToothRequest {
    /** Miller mobility 0..3. */
    @Min(0) @Max(3)
    private Integer mobility;

    /** Glickman furcation 0..3 (null for non-molars). */
    @Min(0) @Max(3)
    private Integer furcation;

    private String notes;

    @Valid
    private List<PeriodontalSiteRequest> sites;
}
