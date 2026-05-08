package com.bs.odontograma.odontogram.entity;

import com.bs.odontograma.odontogram.enums.ToothCondition;
import com.bs.odontograma.odontogram.enums.ToothSurface;
import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Per-surface finding for a tooth (e.g. caries on the M and O surfaces of tooth 36).
 * Used when the diagnosis is surface-specific rather than affecting the whole tooth.
 */
@Entity
@Table(
        name = "tooth_surface_conditions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_surface_tooth_surface",
                columnNames = {"tooth_record_id", "surface"}
        ),
        indexes = {
                @Index(name = "idx_surface_tooth", columnList = "tooth_record_id"),
                @Index(name = "idx_surface_tenant", columnList = "tenant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ToothSurfaceCondition extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tooth_record_id", nullable = false)
    private ToothRecord toothRecord;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private ToothSurface surface;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ToothCondition condition;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
