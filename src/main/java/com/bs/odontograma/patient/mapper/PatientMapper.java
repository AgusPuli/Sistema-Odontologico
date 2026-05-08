package com.bs.odontograma.patient.mapper;

import com.bs.odontograma.patient.dto.request.CreatePatientRequest;
import com.bs.odontograma.patient.dto.response.PatientResponse;
import com.bs.odontograma.patient.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "fullName", expression = "java(patient.getFullName())")
    PatientResponse toResponse(Patient patient);

    @Mapping(target = "active", ignore = true)
    Patient toEntity(CreatePatientRequest request);
}
