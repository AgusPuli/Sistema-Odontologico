package com.bs.odontograma.clinicalsession.repository;

import com.bs.odontograma.clinicalsession.entity.ClinicalSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClinicalSessionRepository extends JpaRepository<ClinicalSession, UUID> {

    Optional<ClinicalSession> findByIdAndTenantId(UUID id, UUID tenantId);

    List<ClinicalSession> findByTenantIdAndPatientIdOrderBySessionDateDesc(UUID tenantId, UUID patientId);

    Page<ClinicalSession> findByTenantId(UUID tenantId, Pageable pageable);
}
