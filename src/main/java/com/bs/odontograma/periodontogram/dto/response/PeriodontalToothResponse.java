package com.bs.odontograma.periodontogram.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PeriodontalToothResponse {
    private UUID id;
    private Integer fdiNumber;
    private Integer mobility;
    private Integer furcation;
    private String notes;
    private List<PeriodontalSiteResponse> sites;
}
