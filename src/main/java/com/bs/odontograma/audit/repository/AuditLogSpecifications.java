package com.bs.odontograma.audit.repository;

import com.bs.odontograma.audit.entity.AuditAction;
import com.bs.odontograma.audit.entity.AuditLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Specifications for AuditLog queries.
 * Used for building dynamic queries with optional filters.
 */
public class AuditLogSpecifications {

    /**
     * Creates a specification for searching audit logs with multiple optional filters.
     *
     * @param tenantId   Required tenant ID filter
     * @param entityType Optional entity type filter
     * @param entityId   Optional entity ID filter
     * @param userEmail  Optional user email filter
     * @param action     Optional action type filter
     * @param startDate  Optional start date filter (inclusive)
     * @param endDate    Optional end date filter (inclusive)
     * @return Specification for the search criteria
     */
    public static Specification<AuditLog> withFilters(
            UUID tenantId,
            String entityType,
            UUID entityId,
            String userEmail,
            AuditAction action,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tenant ID is always required
            predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));

            // Optional entity type filter
            if (entityType != null) {
                predicates.add(criteriaBuilder.equal(root.get("entityType"), entityType));
            }

            // Optional entity ID filter
            if (entityId != null) {
                predicates.add(criteriaBuilder.equal(root.get("entityId"), entityId));
            }

            // Optional user email filter
            if (userEmail != null) {
                predicates.add(criteriaBuilder.equal(root.get("userEmail"), userEmail));
            }

            // Optional action filter
            if (action != null) {
                predicates.add(criteriaBuilder.equal(root.get("action"), action));
            }

            // Optional start date filter
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate));
            }

            // Optional end date filter
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
