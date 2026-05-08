package com.bs.odontograma.treatment.mapper;

import com.bs.odontograma.treatment.dto.response.EstimateItemResponse;
import com.bs.odontograma.treatment.dto.response.EstimateResponse;
import com.bs.odontograma.treatment.dto.response.TreatmentResponse;
import com.bs.odontograma.treatment.entity.Estimate;
import com.bs.odontograma.treatment.entity.EstimateItem;
import com.bs.odontograma.treatment.entity.Treatment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TreatmentMapper {

    @Mapping(target = "specialtyDisplay", expression = "java(treatment.getSpecialty() != null ? treatment.getSpecialty().getDisplayName() : null)")
    TreatmentResponse toResponse(Treatment treatment);

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientFullName", expression = "java(estimate.getPatient() != null ? estimate.getPatient().getFullName() : null)")
    EstimateResponse toResponse(Estimate estimate);

    @Mapping(target = "treatmentId", source = "treatment.id")
    @Mapping(target = "treatmentCode", source = "treatment.code")
    @Mapping(target = "treatmentName", source = "treatment.name")
    EstimateItemResponse toResponse(EstimateItem item);
}
