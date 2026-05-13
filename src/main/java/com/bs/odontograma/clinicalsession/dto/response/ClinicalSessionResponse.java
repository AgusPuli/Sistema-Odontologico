package com.bs.odontograma.clinicalsession.dto.response;

import com.bs.odontograma.clinicalsession.enums.ClinicalSessionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ClinicalSessionResponse {
    private UUID id;
    private UUID tenantId;
    private UUID patientId;
    private String patientFullName;
    private UUID dentistId;
    private String dentistFullName;
    private UUID appointmentId;
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
    private List<SessionProcedureResponse> procedures;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
