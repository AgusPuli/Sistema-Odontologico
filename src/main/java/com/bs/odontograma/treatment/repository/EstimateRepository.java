package com.bs.odontograma.treatment.repository;

import com.bs.odontograma.treatment.entity.Estimate;
import com.bs.odontograma.treatment.enums.EstimateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, UUID> {

    Optional<Estimate> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<Estimate> findByTenantId(UUID tenantId, Pageable pageable);

    List<Estimate> findByTenantIdAndPatientIdOrderByIssueDateDesc(UUID tenantId, UUID patientId);

    long countByTenantIdAndStatusIn(UUID tenantId, Collection<EstimateStatus> statuses);

    @Query("""
            SELECT COALESCE(SUM(e.total), 0)
            FROM Estimate e
            WHERE e.tenantId = :tenantId
              AND e.status IN :statuses
            """)
    BigDecimal sumTotalByTenantIdAndStatusIn(
            @Param("tenantId") UUID tenantId,
            @Param("statuses") Collection<EstimateStatus> statuses
    );
}
