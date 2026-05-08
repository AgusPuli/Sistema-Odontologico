package com.bs.odontograma.treatment.dto.request;

import com.bs.odontograma.shared.enums.DentalSpecialty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTreatmentRequest {

    @Size(max = 200)
    private String name;

    private DentalSpecialty specialty;

    private BigDecimal defaultPrice;

    @Positive
    private Integer durationMinutes;

    private String description;
}
