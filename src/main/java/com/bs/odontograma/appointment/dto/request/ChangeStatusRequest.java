package com.bs.odontograma.appointment.dto.request;

import com.bs.odontograma.appointment.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusRequest {

    @NotNull
    private AppointmentStatus status;
}
