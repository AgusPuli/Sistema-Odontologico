package com.bs.odontograma.clinicalsession.entity;

import com.bs.odontograma.shared.entity.BaseEntity;
import com.bs.odontograma.treatment.entity.Treatment;
import jakarta.persistence.*;
import lombok.*;

/**
 * One procedure performed during a {@link ClinicalSession}.
 * Mirrors EstimateItem but represents what was actually done (no price column —
 * pricing belongs to the estimate / invoice flow).
 */
@Entity
@Table(
        name = "clinical_session_procedures",
        indexes = {
                @Index(name = "idx_csp_session", columnList = "session_id"),
                @Index(name = "idx_csp_treatment", columnList = "treatment_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ClinicalSessionProcedure extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ClinicalSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @Column(name = "fdi_number")
    private Integer fdiNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
