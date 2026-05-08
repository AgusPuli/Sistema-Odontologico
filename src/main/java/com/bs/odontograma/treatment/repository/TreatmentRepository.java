package com.bs.odontograma.treatment.repository;

import com.bs.odontograma.shared.enums.DentalSpecialty;
import com.bs.odontograma.treatment.entity.Treatment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {

    Optional<Treatment> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByTenantIdAndCode(UUID tenantId, String code);

    Page<Treatment> findByTenantId(UUID tenantId, Pageable pageable);

    List<Treatment> findByTenantIdAndSpecialtyAndActiveTrue(UUID tenantId, DentalSpecialty specialty);

    @Query("""
            SELECT t FROM Treatment t
            WHERE t.tenantId = :tenantId
              AND (:specialty IS NULL OR t.specialty = :specialty)
              AND (:search IS NULL OR :search = '' OR
                   LOWER(t.code) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Treatment> search(
            @Param("tenantId") UUID tenantId,
            @Param("specialty") DentalSpecialty specialty,
            @Param("search") String search,
            Pageable pageable
    );

    long countByTenantId(UUID tenantId);
}
