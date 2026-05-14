package com.bs.odontograma.periodontogram.dto.response;

import com.bs.odontograma.periodontogram.enums.PeriodontalSite;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PeriodontalSiteResponse {
    private UUID id;
    private PeriodontalSite site;
    private Integer probingDepth;
    private Integer recession;
    /** Computed: PD + recession. Null if either is missing. */
    private Integer clinicalAttachmentLevel;
    private Boolean bleeding;
    private Boolean suppuration;
    private Boolean plaque;
}
