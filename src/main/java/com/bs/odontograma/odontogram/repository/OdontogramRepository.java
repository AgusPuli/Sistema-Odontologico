package com.bs.odontograma.odontogram.repository;

import com.bs.odontograma.odontogram.entity.Odontogram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OdontogramRepository extends JpaRepository<Odontogram, UUID> {

    Optional<Odontogram> findByIdAndTenantId(UUID id, UUID tenantId);

    List<Odontogram> findByTenantIdAndPatientIdOrderByCreatedAtDesc(UUID tenantId, UUID patientId);

    Optional<Odontogram> findByTenantIdAndPatientIdAndCurrentTrue(UUID tenantId, UUID patientId);
}
