package com.bs.odontograma.odontogram.dto.response;

import com.bs.odontograma.odontogram.enums.ToothCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToothRecordResponse {
    private UUID id;
    private Integer fdiNumber;
    private ToothCondition condition;
    private String observation;
    private List<ToothSurfaceResponse> surfaces;
}
