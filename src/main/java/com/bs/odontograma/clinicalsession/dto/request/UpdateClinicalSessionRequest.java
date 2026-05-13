package com.bs.odontograma.clinicalsession.dto.request;

import com.bs.odontograma.clinicalsession.enums.ClinicalSessionStatus;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Partial update — only non-null fields are applied. Pass {@code procedures}
 * if you want to replace the full procedure list (semantics matches the form's
 * "Guardar" button — the UI ships the current state).
 */
@Data
public class UpdateClinicalSessionRequest {
    private LocalDateTime sessionDate;
    private Integer durationMinutes;

    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer heartRate;

    private String subjective;
    private String objective;
    private String assessment;
    private String plan;

    private Boolean anesthesiaUsed;
    private String anesthesiaType;
    private Integer anesthesiaDoses;

    private String materialsUsed;
    private String generalNotes;
    private String nextAppointmentRecommendation;

    private ClinicalSessionStatus status;

    /** Null = leave procedures untouched. Empty list = clear them. */
    @Valid
    private List<SessionProcedureRequest> procedures;
}
