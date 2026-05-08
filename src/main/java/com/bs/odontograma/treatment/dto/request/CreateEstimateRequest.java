package com.bs.odontograma.treatment.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEstimateRequest {

    @NotNull
    private UUID patientId;

    private LocalDate validUntil;

    private String notes;

    @Valid
    @NotEmpty(message = "Estimate must contain at least one item")
    private List<EstimateItemRequest> items;
}
