package com.bs.odontograma.clinicalsession.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SessionProcedureRequest {
    @NotNull
    private UUID treatmentId;
    /** FDI number (11-48 permanent, 51-85 primary). Null if not tooth-specific. */
    private Integer fdiNumber;
    private String notes;
}
