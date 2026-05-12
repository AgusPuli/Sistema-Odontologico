package com.bs.odontograma.patient.service;

import com.bs.odontograma.patient.dto.request.CreatePatientRequest;
import com.bs.odontograma.patient.dto.request.UpdatePatientRequest;
import com.bs.odontograma.patient.dto.response.PatientResponse;
import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.patient.mapper.PatientMapper;
import com.bs.odontograma.patient.repository.PatientRepository;
import com.bs.odontograma.shared.exception.BusinessRuleViolationException;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PatientService {

    private final PatientRepository repository;
    private final PatientMapper mapper;
    private final TenantContext tenantContext;

    public PatientResponse create(CreatePatientRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        log.info("Creating patient for tenant {}", tenantId);

        if (request.getDocumentNumber() != null && !request.getDocumentNumber().isBlank()
                && repository.existsByTenantIdAndDocumentNumber(tenantId, request.getDocumentNumber())) {
            throw new BusinessRuleViolationException(
                    "A patient with document " + request.getDocumentNumber() + " already exists"
            );
        }

        Patient patient = Patient.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .documentNumber(request.getDocumentNumber())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .healthInsurance(request.getHealthInsurance())
                .insuranceNumber(request.getInsuranceNumber())
                .medicalNotes(request.getMedicalNotes())
                .allergies(request.getAllergies())
                .active(true)
                .build();
        patient.setTenantId(tenantId);

        patient = repository.save(patient);
        return mapper.toResponse(patient);
    }

    @Transactional(readOnly = true)
    public PatientResponse findById(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Patient patient = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", id));
        return mapper.toResponse(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> findAll(String search, Pageable pageable) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Page<Patient> page = (search == null || search.isBlank())
                ? repository.findByTenantId(tenantId, pageable)
                : repository.searchByTenantId(tenantId, search.trim(), pageable);
        return page.map(mapper::toResponse);
    }

    public PatientResponse update(UUID id, UpdatePatientRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Patient patient = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", id));

        if (request.getDocumentNumber() != null
                && !request.getDocumentNumber().equals(patient.getDocumentNumber())
                && repository.existsByTenantIdAndDocumentNumber(tenantId, request.getDocumentNumber())) {
            throw new BusinessRuleViolationException(
                    "A patient with document " + request.getDocumentNumber() + " already exists"
            );
        }

        if (request.getFirstName() != null) patient.setFirstName(request.getFirstName().trim());
        if (request.getLastName() != null) patient.setLastName(request.getLastName().trim());
        if (request.getDocumentNumber() != null) patient.setDocumentNumber(request.getDocumentNumber());
        if (request.getBirthDate() != null) patient.setBirthDate(request.getBirthDate());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getPhone() != null) patient.setPhone(request.getPhone());
        if (request.getEmail() != null) patient.setEmail(request.getEmail());
        if (request.getAddress() != null) patient.setAddress(request.getAddress());
        if (request.getHealthInsurance() != null) patient.setHealthInsurance(request.getHealthInsurance());
        if (request.getInsuranceNumber() != null) patient.setInsuranceNumber(request.getInsuranceNumber());
        if (request.getMedicalNotes() != null) patient.setMedicalNotes(request.getMedicalNotes());
        if (request.getAllergies() != null) patient.setAllergies(request.getAllergies());

        return mapper.toResponse(repository.save(patient));
    }

    public void deactivate(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Patient patient = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", id));
        patient.deactivate();
        repository.save(patient);
    }

    public void activate(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Patient patient = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", id));
        patient.activate();
        repository.save(patient);
    }
}
