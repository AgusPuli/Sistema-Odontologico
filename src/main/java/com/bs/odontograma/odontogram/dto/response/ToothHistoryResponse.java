package com.bs.odontograma.odontogram.dto.response;

import com.bs.odontograma.odontogram.enums.ToothCondition;
import com.bs.odontograma.odontogram.enums.ToothSurface;
import com.bs.odontograma.odontogram.enums.TreatmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToothHistoryResponse {
    private UUID id;
    private UUID toothRecordId;
    private Integer fdiNumber;
    private ToothCondition finding;
    private ToothSurface surface;
    private TreatmentStatus treatmentStatus;
    private String note;
    private String recordedByEmail;
    private LocalDateTime recordedAt;
    private UUID treatmentId;
    private String treatmentCode;
    private String treatmentName;
}
