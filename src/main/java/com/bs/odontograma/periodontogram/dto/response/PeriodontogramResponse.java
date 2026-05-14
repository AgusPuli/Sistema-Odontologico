package com.bs.odontograma.periodontogram.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PeriodontogramResponse {
    private UUID id;
    private UUID tenantId;
    private UUID patientId;
    private LocalDate examDate;
    private Boolean current;
    private String generalNotes;
    private BigDecimal bleedingIndex; // %
    private BigDecimal plaqueIndex;   // %
    private List<PeriodontalToothResponse> teeth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
