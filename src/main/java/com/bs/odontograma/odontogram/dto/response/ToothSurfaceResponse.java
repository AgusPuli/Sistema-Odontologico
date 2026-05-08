package com.bs.odontograma.odontogram.dto.response;

import com.bs.odontograma.odontogram.enums.ToothCondition;
import com.bs.odontograma.odontogram.enums.ToothSurface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToothSurfaceResponse {
    private UUID id;
    private ToothSurface surface;
    private ToothCondition condition;
    private String notes;
}
