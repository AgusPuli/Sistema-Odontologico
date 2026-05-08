package com.bs.odontograma.treatment.entity;

import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.shared.entity.BaseEntity;
import com.bs.odontograma.treatment.enums.EstimateStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Patient estimate (presupuesto). Aggregates treatment items with prices.
 * Total is computed from items on save by {@code recomputeTotal()}.
 */
@Entity
@Table(
        name = "estimates",
        indexes = {
                @Index(name = "idx_estimates_tenant", columnList = "tenant_id"),
                @Index(name = "idx_estimates_patient", columnList = "patient_id"),
                @Index(name = "idx_estimates_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Estimate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "issue_date", nullable = false)
    @Builder.Default
    private LocalDate issueDate = LocalDate.now();

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstimateStatus status = EstimateStatus.DRAFT;

    @Column(precision = 14, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EstimateItem> items = new ArrayList<>();

    public void recomputeTotal() {
        this.total = items.stream()
                .map(EstimateItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
