package com.bs.odontograma.clinicalsession.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SessionProcedureResponse {
    private UUID id;
    private UUID treatmentId;
    private String treatmentCode;
    private String treatmentName;
    private Integer fdiNumber;
    private String notes;
}
