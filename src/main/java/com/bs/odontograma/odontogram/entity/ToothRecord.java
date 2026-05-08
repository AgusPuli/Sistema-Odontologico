package com.bs.odontograma.odontogram.entity;

import com.bs.odontograma.odontogram.enums.ToothCondition;
import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * State of a single tooth on an odontogram.
 *
 * The {@code fdiNumber} follows the FDI two-digit notation:
 *  - Permanent: 11-18, 21-28, 31-38, 41-48
 *  - Primary:   51-55, 61-65, 71-75, 81-85
 *
 * Whole-tooth diagnoses live on {@link #condition}. Surface-specific findings
 * (e.g. caries on M and O of tooth 36) live in {@link ToothSurfaceCondition}.
 */
@Entity
@Table(
        name = "tooth_records",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_tooth_records_odontogram_fdi",
                columnNames = {"odontogram_id", "fdi_number"}
        ),
        indexes = {
                @Index(name = "idx_tooth_records_odontogram", columnList = "odontogram_id"),
                @Index(name = "idx_tooth_records_tenant", columnList = "tenant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ToothRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "odontogram_id", nullable = false)
    private Odontogram odontogram;

    @Column(name = "fdi_number", nullable = false)
    private Integer fdiNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ToothCondition condition = ToothCondition.HEALTHY;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @OneToMany(mappedBy = "toothRecord", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ToothSurfaceCondition> surfaces = new ArrayList<>();

    /**
     * Append-only clinical history. Each entry represents a diagnosis, plan or status change
     * recorded for this specific tooth, ordered by createdAt (BaseEntity).
     */
    @OneToMany(mappedBy = "toothRecord", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ToothHistoryEntry> history = new ArrayList<>();
}
