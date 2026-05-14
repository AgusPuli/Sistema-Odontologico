package com.bs.odontograma.periodontogram.entity;

import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Periodontal exam aggregate. Mirrors the Odontogram pattern: one
 * {@code current=true} per patient, historic versions kept by flipping the
 * flag on archive.
 */
@Entity
@Table(
        name = "periodontograms",
        indexes = {
                @Index(name = "idx_perio_tenant", columnList = "tenant_id"),
                @Index(name = "idx_perio_patient", columnList = "patient_id"),
                @Index(name = "idx_perio_current", columnList = "tenant_id, patient_id, current")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Periodontogram extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "exam_date", nullable = false)
    @Builder.Default
    private LocalDate examDate = LocalDate.now();

    @Column(nullable = false)
    @Builder.Default
    private Boolean current = true;

    @Column(name = "general_notes", columnDefinition = "TEXT")
    private String generalNotes;

    @Column(name = "bleeding_index", precision = 5, scale = 2)
    private BigDecimal bleedingIndex;

    @Column(name = "plaque_index", precision = 5, scale = 2)
    private BigDecimal plaqueIndex;

    @OneToMany(mappedBy = "periodontogram", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PeriodontogramTooth> teeth = new ArrayList<>();

    public void markAsHistorical() {
        this.current = false;
    }

    /**
     * Recomputes BoP% and Plaque% across every site of every tooth.
     * Called by the service after each tooth update.
     */
    public void recomputeIndices() {
        long total = teeth.stream().flatMap(t -> t.getSites().stream()).count();
        if (total == 0) {
            this.bleedingIndex = BigDecimal.ZERO;
            this.plaqueIndex = BigDecimal.ZERO;
            return;
        }
        long bleeding = teeth.stream().flatMap(t -> t.getSites().stream())
                .filter(s -> Boolean.TRUE.equals(s.getBleeding())).count();
        long plaque = teeth.stream().flatMap(t -> t.getSites().stream())
                .filter(s -> Boolean.TRUE.equals(s.getPlaque())).count();

        this.bleedingIndex = BigDecimal.valueOf(bleeding * 100.0 / total)
                .setScale(2, java.math.RoundingMode.HALF_UP);
        this.plaqueIndex = BigDecimal.valueOf(plaque * 100.0 / total)
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
