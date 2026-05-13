package com.bs.odontograma.clinicalsession.dto.request;

import com.bs.odontograma.clinicalsession.enums.ClinicalSessionStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CreateClinicalSessionRequest {
    @NotNull
    private UUID patientId;
    /** Dentist that performed the session. Defaults to current user if null. */
    private UUID dentistId;
    /** Optional link to the originating appointment. */
    private UUID appointmentId;
    private LocalDateTime sessionDate;
    private Integer durationMinutes;

    // Vital signs
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer heartRate;

    // SOAP
    private String subjective;
    private String objective;
    private String assessment;
    private String plan;

    // Anesthesia
    private Boolean anesthesiaUsed;
    private String anesthesiaType;
    private Integer anesthesiaDoses;

    // Other
    private String materialsUsed;
    private String generalNotes;
    private String nextAppointmentRecommendation;

    /** DRAFT (default) or COMPLETED if the dentist signs off immediately. */
    private ClinicalSessionStatus status;

    /** Procedures performed in this session. */
    @Valid
    private List<SessionProcedureRequest> procedures = new ArrayList<>();
}
