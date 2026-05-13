package com.bs.odontograma.clinicalsession.service;

import com.bs.odontograma.appointment.entity.Appointment;
import com.bs.odontograma.appointment.repository.AppointmentRepository;
import com.bs.odontograma.auth.entity.User;
import com.bs.odontograma.auth.repository.UserRepository;
import com.bs.odontograma.clinicalsession.dto.request.CreateClinicalSessionRequest;
import com.bs.odontograma.clinicalsession.dto.request.SessionProcedureRequest;
import com.bs.odontograma.clinicalsession.dto.request.UpdateClinicalSessionRequest;
import com.bs.odontograma.clinicalsession.dto.response.ClinicalSessionResponse;
import com.bs.odontograma.clinicalsession.entity.ClinicalSession;
import com.bs.odontograma.clinicalsession.entity.ClinicalSessionProcedure;
import com.bs.odontograma.clinicalsession.enums.ClinicalSessionStatus;
import com.bs.odontograma.clinicalsession.mapper.ClinicalSessionMapper;
import com.bs.odontograma.clinicalsession.repository.ClinicalSessionRepository;
import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.patient.repository.PatientRepository;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.TenantContext;
import com.bs.odontograma.shared.security.UserPrincipal;
import com.bs.odontograma.treatment.entity.Treatment;
import com.bs.odontograma.treatment.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClinicalSessionService {

    private final ClinicalSessionRepository repository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final TreatmentRepository treatmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClinicalSessionMapper mapper;
    private final TenantContext tenantContext;

    public ClinicalSessionResponse create(CreateClinicalSessionRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        Patient patient = patientRepository.findByIdAndTenantId(request.getPatientId(), tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", request.getPatientId()));

        // Dentist defaults to current user when not supplied
        UUID dentistId = request.getDentistId() != null ? request.getDentistId() : currentUserId();
        User dentist = userRepository.findById(dentistId)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Dentist", dentistId));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findByIdAndTenantId(request.getAppointmentId(), tenantId)
                    .orElseThrow(() -> new EntityNotFoundException("Appointment", request.getAppointmentId()));
        }

        ClinicalSession session = ClinicalSession.builder()
                .patient(patient)
                .dentist(dentist)
                .appointment(appointment)
                .sessionDate(request.getSessionDate() != null ? request.getSessionDate() : LocalDateTime.now())
                .durationMinutes(request.getDurationMinutes())
                .bloodPressureSystolic(request.getBloodPressureSystolic())
                .bloodPressureDiastolic(request.getBloodPressureDiastolic())
                .heartRate(request.getHeartRate())
                .subjective(request.getSubjective())
                .objective(request.getObjective())
                .assessment(request.getAssessment())
                .plan(request.getPlan())
                .anesthesiaUsed(Boolean.TRUE.equals(request.getAnesthesiaUsed()))
                .anesthesiaType(request.getAnesthesiaType())
                .anesthesiaDoses(request.getAnesthesiaDoses())
                .materialsUsed(request.getMaterialsUsed())
                .generalNotes(request.getGeneralNotes())
                .nextAppointmentRecommendation(request.getNextAppointmentRecommendation())
                .status(request.getStatus() != null ? request.getStatus() : ClinicalSessionStatus.DRAFT)
                .build();
        session.setTenantId(tenantId);

        // Procedures
        for (SessionProcedureRequest procReq : request.getProcedures()) {
            session.getProcedures().add(buildProcedure(session, procReq, tenantId));
        }

        session = repository.save(session);
        log.info("Created clinical session {} for patient {}", session.getId(), patient.getId());
        return mapper.toResponse(session);
    }

    @Transactional(readOnly = true)
    public ClinicalSessionResponse findById(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return mapper.toResponse(loadOwned(id, tenantId));
    }

    @Transactional(readOnly = true)
    public List<ClinicalSessionResponse> findByPatient(UUID patientId) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository.findByTenantIdAndPatientIdOrderBySessionDateDesc(tenantId, patientId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Partial update. Non-null fields overwrite. If {@code procedures} is provided
     * (even as an empty list) it replaces the whole list — matches the form UX
     * where the user adds/removes items and clicks "Guardar".
     */
    public ClinicalSessionResponse update(UUID id, UpdateClinicalSessionRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        ClinicalSession session = loadOwned(id, tenantId);

        if (request.getSessionDate() != null) session.setSessionDate(request.getSessionDate());
        if (request.getDurationMinutes() != null) session.setDurationMinutes(request.getDurationMinutes());

        if (request.getBloodPressureSystolic() != null) session.setBloodPressureSystolic(request.getBloodPressureSystolic());
        if (request.getBloodPressureDiastolic() != null) session.setBloodPressureDiastolic(request.getBloodPressureDiastolic());
        if (request.getHeartRate() != null) session.setHeartRate(request.getHeartRate());

        if (request.getSubjective() != null) session.setSubjective(request.getSubjective());
        if (request.getObjective() != null) session.setObjective(request.getObjective());
        if (request.getAssessment() != null) session.setAssessment(request.getAssessment());
        if (request.getPlan() != null) session.setPlan(request.getPlan());

        if (request.getAnesthesiaUsed() != null) session.setAnesthesiaUsed(request.getAnesthesiaUsed());
        if (request.getAnesthesiaType() != null) session.setAnesthesiaType(request.getAnesthesiaType());
        if (request.getAnesthesiaDoses() != null) session.setAnesthesiaDoses(request.getAnesthesiaDoses());

        if (request.getMaterialsUsed() != null) session.setMaterialsUsed(request.getMaterialsUsed());
        if (request.getGeneralNotes() != null) session.setGeneralNotes(request.getGeneralNotes());
        if (request.getNextAppointmentRecommendation() != null)
            session.setNextAppointmentRecommendation(request.getNextAppointmentRecommendation());

        if (request.getStatus() != null) session.setStatus(request.getStatus());

        if (request.getProcedures() != null) {
            // orphanRemoval=true on the relationship will delete removed children.
            session.getProcedures().clear();
            for (SessionProcedureRequest procReq : request.getProcedures()) {
                session.getProcedures().add(buildProcedure(session, procReq, tenantId));
            }
        }

        return mapper.toResponse(repository.save(session));
    }

    public void delete(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        ClinicalSession session = loadOwned(id, tenantId);
        repository.delete(session);
        log.info("Deleted clinical session {}", id);
    }

    // ---------- helpers ----------

    private ClinicalSession loadOwned(UUID id, UUID tenantId) {
        return repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("ClinicalSession", id));
    }

    private ClinicalSessionProcedure buildProcedure(
            ClinicalSession session, SessionProcedureRequest req, UUID tenantId
    ) {
        Treatment t = treatmentRepository.findByIdAndTenantId(req.getTreatmentId(), tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Treatment", req.getTreatmentId()));
        ClinicalSessionProcedure proc = ClinicalSessionProcedure.builder()
                .session(session)
                .treatment(t)
                .fdiNumber(req.getFdiNumber())
                .notes(req.getNotes())
                .build();
        proc.setTenantId(tenantId);
        return proc;
    }

    private UUID currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal p) {
            return p.getId();
        }
        throw new IllegalStateException("No authenticated user in context");
    }
}
