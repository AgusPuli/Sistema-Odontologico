package com.bs.odontograma.treatment.entity;

import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "estimate_items",
        indexes = {
                @Index(name = "idx_estimate_items_estimate", columnList = "estimate_id"),
                @Index(name = "idx_estimate_items_treatment", columnList = "treatment_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EstimateItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estimate_id", nullable = false)
    private Estimate estimate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    /**
     * FDI tooth number this item applies to (optional). Lets the estimate point to a
     * specific tooth so the patient sees "Conducto - pieza 36" on the printout.
     */
    @Column(name = "fdi_number")
    private Integer fdiNumber;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "unit_price", precision = 14, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal subtotal;

    public void recomputeSubtotal() {
        if (unitPrice == null) {
            this.subtotal = BigDecimal.ZERO;
            return;
        }
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity == null ? 1 : quantity));
    }
}
