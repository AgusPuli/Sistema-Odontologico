package com.bs.odontograma.odontogram.dto.response;

import com.bs.odontograma.odontogram.enums.Dentition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OdontogramResponse {
    private UUID id;
    private UUID tenantId;
    private UUID patientId;
    private Dentition dentition;
    private String generalNotes;
    private Boolean current;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ToothRecordResponse> teeth;
}
