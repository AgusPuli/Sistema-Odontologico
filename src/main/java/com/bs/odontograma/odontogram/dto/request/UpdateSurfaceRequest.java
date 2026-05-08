package com.bs.odontograma.odontogram.dto.request;

import com.bs.odontograma.odontogram.enums.ToothCondition;
import com.bs.odontograma.odontogram.enums.ToothSurface;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSurfaceRequest {

    @NotNull
    private ToothSurface surface;

    @NotNull
    private ToothCondition condition;

    private String notes;
}
