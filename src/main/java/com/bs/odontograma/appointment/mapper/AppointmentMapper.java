package com.bs.odontograma.appointment.mapper;

import com.bs.odontograma.appointment.dto.response.AppointmentResponse;
import com.bs.odontograma.appointment.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientFullName", source = "patient", qualifiedByName = "patientFullName")
    @Mapping(target = "dentistId", source = "dentist.id")
    @Mapping(target = "dentistFullName", source = "dentist", qualifiedByName = "userFullName")
    AppointmentResponse toResponse(Appointment appointment);

    @Named("patientFullName")
    default String patientFullName(com.bs.odontograma.patient.entity.Patient patient) {
        return patient == null ? null : patient.getFullName();
    }

    @Named("userFullName")
    default String userFullName(com.bs.odontograma.auth.entity.User user) {
        return user == null ? null : user.getFullName();
    }
}
