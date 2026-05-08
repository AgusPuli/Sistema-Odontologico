package com.bs.odontograma.appointment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleRequest {

    @NotNull
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime appointmentTime;
}
