package com.bs.odontograma.periodontogram.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreatePeriodontogramRequest {
    @NotNull
    private UUID patientId;
    private LocalDate examDate;
    private String generalNotes;
}
