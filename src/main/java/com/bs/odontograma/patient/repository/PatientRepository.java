package com.bs.odontograma.patient.repository;

import com.bs.odontograma.patient.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<Patient> findByTenantId(UUID tenantId, Pageable pageable);

    boolean existsByTenantIdAndDocumentNumber(UUID tenantId, String documentNumber);

    long countByTenantId(UUID tenantId);

    long countByTenantIdAndActiveTrue(UUID tenantId);

    @Query("""
            SELECT p FROM Patient p
            WHERE p.tenantId = :tenantId
              AND (:search IS NULL OR :search = '' OR
                   LOWER(p.firstName)      LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(p.lastName)       LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(p.documentNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(p.email)          LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(p.phone)          LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Patient> searchByTenantId(
            @Param("tenantId") UUID tenantId,
            @Param("search") String search,
            Pageable pageable
    );
}
