package com.bs.odontograma.treatment.dto.response;

import com.bs.odontograma.shared.enums.DentalSpecialty;
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
public class TreatmentResponse {
    private UUID id;
    private UUID tenantId;
    private String code;
    private String name;
    private DentalSpecialty specialty;
    private String specialtyDisplay;
    private BigDecimal defaultPrice;
    private Integer durationMinutes;
    private String description;
    private Boolean active;
}
