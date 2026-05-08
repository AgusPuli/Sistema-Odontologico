package com.bs.odontograma.appointment.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {

    private UUID dentistId;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    @Positive
    private Integer durationMinutes;

    @Size(max = 200)
    private String reason;

    private String notes;
}
