package com.bs.odontograma.odontogram.mapper;

import com.bs.odontograma.odontogram.dto.response.OdontogramResponse;
import com.bs.odontograma.odontogram.dto.response.ToothHistoryResponse;
import com.bs.odontograma.odontogram.dto.response.ToothRecordResponse;
import com.bs.odontograma.odontogram.dto.response.ToothSurfaceResponse;
import com.bs.odontograma.odontogram.entity.Odontogram;
import com.bs.odontograma.odontogram.entity.ToothHistoryEntry;
import com.bs.odontograma.odontogram.entity.ToothRecord;
import com.bs.odontograma.odontogram.entity.ToothSurfaceCondition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OdontogramMapper {

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "current", expression = "java(odontogram.isCurrent())")
    OdontogramResponse toResponse(Odontogram odontogram);

    ToothRecordResponse toResponse(ToothRecord toothRecord);

    ToothSurfaceResponse toResponse(ToothSurfaceCondition surface);

    @Mapping(target = "toothRecordId", source = "toothRecord.id")
    @Mapping(target = "fdiNumber", source = "toothRecord.fdiNumber")
    @Mapping(target = "recordedAt", source = "createdAt")
    @Mapping(target = "treatmentId", source = "treatment.id")
    @Mapping(target = "treatmentCode", source = "treatment.code")
    @Mapping(target = "treatmentName", source = "treatment.name")
    ToothHistoryResponse toResponse(ToothHistoryEntry entry);
}
