package com.bs.odontograma.medicalhistory.repository;

import com.bs.odontograma.medicalhistory.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, UUID> {

    Optional<MedicalHistory> findByTenantIdAndPatientId(UUID tenantId, UUID patientId);

    boolean existsByTenantIdAndPatientId(UUID tenantId, UUID patientId);
}
