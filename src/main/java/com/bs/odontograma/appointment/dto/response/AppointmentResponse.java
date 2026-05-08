package com.bs.odontograma.appointment.dto.response;

import com.bs.odontograma.appointment.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private UUID id;
    private UUID tenantId;
    private UUID patientId;
    private String patientFullName;
    private UUID dentistId;
    private String dentistFullName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Integer durationMinutes;
    private String reason;
    private String notes;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
}
