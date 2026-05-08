package com.bs.odontograma.odontogram.entity;

import com.bs.odontograma.odontogram.enums.ToothCondition;
import com.bs.odontograma.odontogram.enums.ToothSurface;
import com.bs.odontograma.odontogram.enums.TreatmentStatus;
import com.bs.odontograma.shared.entity.BaseEntity;
import com.bs.odontograma.treatment.entity.Treatment;
import jakarta.persistence.*;
import lombok.*;

/**
 * Immutable clinical history entry for a single tooth.
 *
 * Every time a tooth is modified (diagnosis, plan or treatment status change), one of these
 * is appended. This is the data source for the patient's "Plan de tratamiento" view as well
 * as for the per-tooth clinical history shown when a tooth is clicked.
 *
 * Entries should never be edited or deleted in the normal flow — corrections are added as
 * NEW entries, preserving the audit trail.
 */
@Entity
@Table(
        name = "tooth_history_entries",
        indexes = {
                @Index(name = "idx_tooth_history_tooth", columnList = "tooth_record_id"),
                @Index(name = "idx_tooth_history_tenant", columnList = "tenant_id"),
                @Index(name = "idx_tooth_history_status", columnList = "treatment_status")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ToothHistoryEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tooth_record_id", nullable = false)
    private ToothRecord toothRecord;

    /**
     * Whole-tooth condition at the time of the entry.
     * Mirrors {@link ToothRecord#getCondition()} so historical entries remain self-contained
     * even after the tooth's current state moves on.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "finding", nullable = false, length = 30)
    private ToothCondition finding;

    /**
     * Optional surface the finding refers to (M, D, V, L, O, I).
     * Null when the diagnosis applies to the whole tooth.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 5)
    private ToothSurface surface;

    @Enumerated(EnumType.STRING)
    @Column(name = "treatment_status", nullable = false, length = 20)
    @Builder.Default
    private TreatmentStatus treatmentStatus = TreatmentStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String note;

    /**
     * Email of the practitioner who recorded the entry. Captured at write time so the
     * history survives even if the user account is later renamed or deleted.
     */
    @Column(name = "recorded_by_email", length = 255)
    private String recordedByEmail;

    /**
     * Optional link to the catalog Treatment performed/planned for this tooth.
     * Lets the "Plan de tratamiento" view show the procedure name and its price.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;
}
