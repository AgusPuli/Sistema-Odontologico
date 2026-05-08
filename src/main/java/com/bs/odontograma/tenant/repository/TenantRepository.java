package com.bs.odontograma.tenant.repository;

import com.bs.odontograma.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByEmail(String email);

    List<Tenant> findByActiveTrue();

    boolean existsByEmail(String email);

    boolean existsByTaxId(String taxId);
}