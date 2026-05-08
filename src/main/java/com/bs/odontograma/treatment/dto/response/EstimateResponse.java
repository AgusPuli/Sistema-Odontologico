package com.bs.odontograma.treatment.dto.response;

import com.bs.odontograma.treatment.enums.EstimateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateResponse {
    private UUID id;
    private UUID tenantId;
    private UUID patientId;
    private String patientFullName;
    private LocalDate issueDate;
    private LocalDate validUntil;
    private EstimateStatus status;
    private BigDecimal total;
    private String notes;
    private LocalDateTime createdAt;
    private List<EstimateItemResponse> items;
}
