package com.bs.odontograma.periodontogram.entity;

import com.bs.odontograma.periodontogram.enums.PeriodontalSite;
import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Measurement at a single periodontal site (1 of 6 per tooth).
 * CAL (clinical attachment level) = probingDepth + recession — computed at the
 * mapper/service layer, not stored, to avoid drift.
 */
@Entity
@Table(
        name = "periodontogram_sites",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tooth_id", "site"}),
        indexes = {
                @Index(name = "idx_perio_site_tooth", columnList = "tooth_id"),
                @Index(name = "idx_perio_site_tenant", columnList = "tenant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PeriodontogramSite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tooth_id", nullable = false)
    private PeriodontogramTooth tooth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private PeriodontalSite site;

    @Column(name = "probing_depth")
    private Integer probingDepth; // mm

    @Column
    private Integer recession; // mm (positive = recession, negative = overgrowth)

    @Column(nullable = false)
    @Builder.Default
    private Boolean bleeding = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean suppuration = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean plaque = false;
}
