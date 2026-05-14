package com.bs.odontograma.periodontogram.entity;

import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Tooth-level periodontal data (mobility, furcation) + 6 site measurements.
 * Sites are eagerly fetched because we always render the whole row in the UI.
 */
@Entity
@Table(
        name = "periodontogram_teeth",
        uniqueConstraints = @UniqueConstraint(columnNames = {"periodontogram_id", "fdi_number"}),
        indexes = {
                @Index(name = "idx_perio_tooth_parent", columnList = "periodontogram_id"),
                @Index(name = "idx_perio_tooth_tenant", columnList = "tenant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PeriodontogramTooth extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodontogram_id", nullable = false)
    private Periodontogram periodontogram;

    @Column(name = "fdi_number", nullable = false)
    private Integer fdiNumber;

    /** Miller mobility 0..3. */
    @Column
    private Integer mobility;

    /** Glickman furcation 0..3. Null for non-molars. */
    @Column
    private Integer furcation;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "tooth", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<PeriodontogramSite> sites = new ArrayList<>();
}
