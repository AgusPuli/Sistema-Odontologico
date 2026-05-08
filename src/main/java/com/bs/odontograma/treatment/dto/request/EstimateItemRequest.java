package com.bs.odontograma.treatment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstimateItemRequest {

    @NotNull
    private UUID treatmentId;

    private Integer fdiNumber;

    @Positive
    private Integer quantity;

    /**
     * Optional. If null, falls back to {@code Treatment#getDefaultPrice()} on the server.
     */
    private BigDecimal unitPrice;
}
