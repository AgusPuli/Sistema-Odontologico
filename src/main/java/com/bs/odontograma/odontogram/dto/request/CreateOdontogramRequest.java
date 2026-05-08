package com.bs.odontograma.odontogram.dto.request;

import com.bs.odontograma.odontogram.enums.Dentition;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOdontogramRequest {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Dentition is required")
    private Dentition dentition;

    private String generalNotes;
}
