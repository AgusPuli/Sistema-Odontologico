package com.bs.odontograma.medicalhistory.mapper;

import com.bs.odontograma.medicalhistory.dto.response.MedicalHistoryResponse;
import com.bs.odontograma.medicalhistory.entity.MedicalHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicalHistoryMapper {
    MedicalHistoryResponse toResponse(MedicalHistory entity);
}
