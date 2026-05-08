package com.bs.odontograma.odontogram.entity;

import com.bs.odontograma.odontogram.enums.Dentition;
import com.bs.odontograma.patient.entity.Patient;
import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Patient odontogram aggregate root.
 *
 * Each odontogram belongs to exactly one patient and contains the per-tooth state
 * (32 permanent teeth, 20 primary, or both for mixed dentition).
 *
 * Design notes:
 *  - One patient may have several historical odontograms (initial vs follow-up snapshots),
 *    so we don't enforce a 1:1 unique constraint here. We expose a "current" pointer at
 *    the patient level via {@link com.bs.odontograma.odontogram.repository.OdontogramRepository#findCurrentByPatientId}.
 *  - All children (teeth and surface findings) cascade for save/delete operations.
 */
@Entity
@Table(
        name = "odontograms",
        indexes = {
                @Index(name = "idx_odontograms_tenant", columnList = "tenant_id"),
                @Index(name = "idx_odontograms_patient", columnList = "patient_id"),
                @Index(name = "idx_odontograms_tenant_patient", columnList = "tenant_id, patient_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Odontogram extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Dentition dentition = Dentition.PERMANENT;

    @Column(name = "general_notes", columnDefinition = "TEXT")
    private String generalNotes;

    @Column(name = "is_current", nullable = false)
    @Builder.Default
    private Boolean current = true;

    @OneToMany(mappedBy = "odontogram", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ToothRecord> teeth = new ArrayList<>();

    public boolean isCurrent() {
        return Boolean.TRUE.equals(current);
    }

    public void markAsHistorical() {
        this.current = false;
    }
}
