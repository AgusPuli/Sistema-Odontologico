package com.bs.odontograma.appointment.enums;

import lombok.Getter;

/**
 * State machine for an appointment.
 *
 *   SCHEDULED  ─┬─► CONFIRMED ─┬─► CHECKED_IN ─► COMPLETED   (terminal)
 *               │              └─► NO_SHOW                   (terminal)
 *               ├─► CHECKED_IN ─► COMPLETED
 *               ├─► CANCELLED                                (terminal)
 *               ├─► NO_SHOW                                  (terminal)
 *               └─► RESCHEDULED ─► (loops back to SCHEDULED)
 */
@Getter
public enum AppointmentStatus {
    SCHEDULED("Agendado"),
    CONFIRMED("Confirmado"),
    CHECKED_IN("Atendiendo"),
    COMPLETED("Completado"),
    CANCELLED("Cancelado"),
    NO_SHOW("No asistió"),
    RESCHEDULED("Reagendado");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public boolean canTransitionTo(AppointmentStatus target) {
        return switch (this) {
            case SCHEDULED -> target == CONFIRMED || target == CHECKED_IN
                    || target == CANCELLED || target == NO_SHOW || target == RESCHEDULED;
            case CONFIRMED -> target == CHECKED_IN || target == CANCELLED
                    || target == NO_SHOW || target == RESCHEDULED;
            case CHECKED_IN -> target == COMPLETED || target == CANCELLED;
            case RESCHEDULED -> target == SCHEDULED;
            case COMPLETED, CANCELLED, NO_SHOW -> false;
        };
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }
}
