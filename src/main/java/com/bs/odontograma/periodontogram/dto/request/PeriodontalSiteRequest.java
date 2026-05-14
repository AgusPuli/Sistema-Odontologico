package com.bs.odontograma.periodontogram.dto.request;

import com.bs.odontograma.periodontogram.enums.PeriodontalSite;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PeriodontalSiteRequest {
    @NotNull
    private PeriodontalSite site;

    @Min(0) @Max(20)
    private Integer probingDepth; // mm

    @Min(-5) @Max(20)
    private Integer recession; // mm

    private Boolean bleeding;
    private Boolean suppuration;
    private Boolean plaque;
}
