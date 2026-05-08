package com.bs.odontograma.appointment.service;

import com.bs.odontograma.appointment.dto.request.CreateAppointmentRequest;
import com.bs.odontograma.appointment.dto.request.RescheduleRequest;
import com.bs.odontograma.appointment.dto.request.UpdateAppointmentRequest;
import com.bs.odontograma.appointment.dto.response.AppointmentResponse;
import com.bs.odontograma.appointment.entity.Appointment;
import com.bs.odontograma.appointment.enums.AppointmentStatus;
import com.bs.odontograma.appointment.mapper.AppointmentMapper;
import com.bs.odontograma.appointment.repository.AppointmentRepository;
import com.bs.odontograma.auth.entity.User;
import com.bs.odontograma.auth.repository.UserRepository;
import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.patient.repository.PatientRepository;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final TenantContext tenantContext;

    public AppointmentResponse create(CreateAppointmentRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        Patient patient = patientRepository.findByIdAndTenantId(request.getPatientId(), tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", request.getPatientId()));

        User dentist = null;
        if (request.getDentistId() != null) {
            dentist = userRepository.findById(request.getDentistId())
                    .filter(u -> u.getTenantId().equals(tenantId))
                    .orElseThrow(() -> new EntityNotFoundException("Dentist", request.getDentistId()));
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .dentist(dentist)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 30)
                .reason(request.getReason())
                .notes(request.getNotes())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        return mapper.toResponse(repository.save(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentResponse findById(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return mapper.toResponse(repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", id)));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> findAll(Pageable pageable) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantId(tenantId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findByDate(LocalDate date) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantIdAndAppointmentDate(tenantId, date)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findByDateRange(LocalDate from, LocalDate to) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantIdAndAppointmentDateBetween(tenantId, from, to)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findByPatient(UUID patientId) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantIdAndPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(tenantId, patientId)
                .stream().map(mapper::toResponse).toList();
    }

    public AppointmentResponse update(UUID id, UpdateAppointmentRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Appointment a = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", id));

        if (request.getDentistId() != null) {
            User dentist = userRepository.findById(request.getDentistId())
                    .filter(u -> u.getTenantId().equals(tenantId))
                    .orElseThrow(() -> new EntityNotFoundException("Dentist", request.getDentistId()));
            a.setDentist(dentist);
        }
        if (request.getAppointmentDate() != null) a.setAppointmentDate(request.getAppointmentDate());
        if (request.getAppointmentTime() != null) a.setAppointmentTime(request.getAppointmentTime());
        if (request.getDurationMinutes() != null) a.setDurationMinutes(request.getDurationMinutes());
        if (request.getReason() != null) a.setReason(request.getReason());
        if (request.getNotes() != null) a.setNotes(request.getNotes());

        return mapper.toResponse(repository.save(a));
    }

    public AppointmentResponse changeStatus(UUID id, AppointmentStatus newStatus) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Appointment a = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", id));
        a.transitionTo(newStatus);
        return mapper.toResponse(repository.save(a));
    }

    public AppointmentResponse reschedule(UUID id, RescheduleRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Appointment a = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", id));
        a.reschedule(request.getAppointmentDate(), request.getAppointmentTime());
        return mapper.toResponse(repository.save(a));
    }

    public void delete(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Appointment a = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", id));
        repository.delete(a);
    }
}
