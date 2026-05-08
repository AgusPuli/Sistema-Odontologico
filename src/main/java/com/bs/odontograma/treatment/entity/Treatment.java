package com.bs.odontograma.treatment.entity;

import com.bs.odontograma.shared.entity.BaseEntity;
import com.bs.odontograma.shared.enums.DentalSpecialty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Catalog entry: a procedure offered by the clinic (e.g. "Conducto unirradicular",
 * "Brackets autoligado"). Each clinic owns its catalog and adjusts default prices
 * independently. New clinics can clone the default catalog via
 * {@code POST /api/treatments/seed-defaults}.
 */
@Entity
@Table(
        name = "treatments",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_treatments_tenant_code",
                columnNames = {"tenant_id", "code"}
        ),
        indexes = {
                @Index(name = "idx_treatments_tenant", columnList = "tenant_id"),
                @Index(name = "idx_treatments_specialty", columnList = "specialty"),
                @Index(name = "idx_treatments_tenant_specialty", columnList = "tenant_id, specialty")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Treatment extends BaseEntity {

    /**
     * Internal short code (e.g. "ENDO-1R", "ORTHO-BRK-AUTO"). Unique per tenant.
     */
    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DentalSpecialty specialty;

    @Column(name = "default_price", precision = 14, scale = 2)
    private BigDecimal defaultPrice;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public void deactivate() { this.active = false; }
    public void activate() { this.active = true; }
}
