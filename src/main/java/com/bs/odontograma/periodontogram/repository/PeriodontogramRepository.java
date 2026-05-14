package com.bs.odontograma.periodontogram.repository;

import com.bs.odontograma.periodontogram.entity.Periodontogram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PeriodontogramRepository extends JpaRepository<Periodontogram, UUID> {

    Optional<Periodontogram> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<Periodontogram> findByTenantIdAndPatientIdAndCurrentTrue(UUID tenantId, UUID patientId);

    List<Periodontogram> findByTenantIdAndPatientIdOrderByExamDateDesc(UUID tenantId, UUID patientId);
}
