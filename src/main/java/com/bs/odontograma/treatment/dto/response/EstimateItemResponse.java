package com.bs.odontograma.treatment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateItemResponse {
    private UUID id;
    private UUID treatmentId;
    private String treatmentCode;
    private String treatmentName;
    private Integer fdiNumber;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
