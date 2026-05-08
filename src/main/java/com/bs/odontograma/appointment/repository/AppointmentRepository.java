package com.bs.odontograma.appointment.repository;

import com.bs.odontograma.appointment.entity.Appointment;
import com.bs.odontograma.appointment.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    Optional<Appointment> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<Appointment> findByTenantId(UUID tenantId, Pageable pageable);

    List<Appointment> findByTenantIdAndAppointmentDate(UUID tenantId, LocalDate date);

    List<Appointment> findByTenantIdAndAppointmentDateBetween(
            UUID tenantId,
            LocalDate from,
            LocalDate to
    );

    List<Appointment> findByTenantIdAndPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(
            UUID tenantId, UUID patientId
    );

    Page<Appointment> findByTenantIdAndStatus(UUID tenantId, AppointmentStatus status, Pageable pageable);

    long countByTenantIdAndAppointmentDate(UUID tenantId, LocalDate date);

    long countByTenantIdAndAppointmentDateBetween(UUID tenantId, LocalDate from, LocalDate to);

    long countByTenantIdAndAppointmentDateGreaterThanEqualAndStatusIn(
            UUID tenantId, LocalDate from, java.util.Collection<AppointmentStatus> statuses
    );
}
