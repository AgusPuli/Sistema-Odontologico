package com.bs.odontograma.treatment.repository;

import com.bs.odontograma.treatment.entity.Estimate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, UUID> {

    Optional<Estimate> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<Estimate> findByTenantId(UUID tenantId, Pageable pageable);

    List<Estimate> findByTenantIdAndPatientIdOrderByIssueDateDesc(UUID tenantId, UUID patientId);
}
