package com.bs.odontograma.clinicalsession.mapper;

import com.bs.odontograma.clinicalsession.dto.response.ClinicalSessionResponse;
import com.bs.odontograma.clinicalsession.dto.response.SessionProcedureResponse;
import com.bs.odontograma.clinicalsession.entity.ClinicalSession;
import com.bs.odontograma.clinicalsession.entity.ClinicalSessionProcedure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClinicalSessionMapper {

    @Mapping(target = "patientId", expression = "java(session.getPatient().getId())")
    @Mapping(target = "patientFullName", expression = "java(session.getPatient().getFullName())")
    @Mapping(target = "dentistId", expression = "java(session.getDentist().getId())")
    @Mapping(target = "dentistFullName", expression = "java(session.getDentist().getFirstName() + \" \" + (session.getDentist().getLastName() == null ? \"\" : session.getDentist().getLastName()))")
    @Mapping(target = "appointmentId", expression = "java(session.getAppointment() == null ? null : session.getAppointment().getId())")
    @Mapping(target = "procedures", expression = "java(mapProcedures(session.getProcedures()))")
    ClinicalSessionResponse toResponse(ClinicalSession session);

    @Mapping(target = "treatmentId", expression = "java(p.getTreatment().getId())")
    @Mapping(target = "treatmentCode", expression = "java(p.getTreatment().getCode())")
    @Mapping(target = "treatmentName", expression = "java(p.getTreatment().getName())")
    SessionProcedureResponse toProcedureResponse(ClinicalSessionProcedure p);

    default List<SessionProcedureResponse> mapProcedures(List<ClinicalSessionProcedure> procs) {
        return procs == null ? List.of() : procs.stream().map(this::toProcedureResponse).toList();
    }
}
