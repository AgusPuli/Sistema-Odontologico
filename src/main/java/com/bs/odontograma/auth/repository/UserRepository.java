package com.bs.odontograma.auth.repository;

import com.bs.odontograma.auth.entity.User;
import com.bs.odontograma.auth.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity.
 * Email is globally unique across all tenants.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByTenantId(UUID tenantId);

    List<User> findByTenantId(UUID tenantId);

    Page<User> findByTenantId(UUID tenantId, Pageable pageable);

    /**
     * Find active users with a specific role in a tenant.
     */
    List<User> findByTenantIdAndRoleAndActiveTrue(UUID tenantId, UserRole role);

    List<User> findByTenantIdAndActiveTrue(UUID tenantId);

    /**
     * Search users by tenant ID and optional search term.
     * Searches in email, firstName, and lastName.
     */
    @Query("""
        SELECT u FROM User u
        WHERE u.tenantId = :tenantId
        AND (:search IS NULL OR :search = '' OR
             LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<User> searchByTenantId(
            @Param("tenantId") UUID tenantId,
            @Param("search") String search,
            Pageable pageable
    );
}
