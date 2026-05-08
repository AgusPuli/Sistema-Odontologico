package com.bs.odontograma.dashboard.service;

import com.bs.odontograma.appointment.enums.AppointmentStatus;
import com.bs.odontograma.appointment.repository.AppointmentRepository;
import com.bs.odontograma.dashboard.dto.DashboardStatsResponse;
import com.bs.odontograma.odontogram.enums.TreatmentStatus;
import com.bs.odontograma.odontogram.repository.ToothHistoryEntryRepository;
import com.bs.odontograma.patient.repository.PatientRepository;
import com.bs.odontograma.shared.security.TenantContext;
import com.bs.odontograma.treatment.enums.EstimateStatus;
import com.bs.odontograma.treatment.repository.EstimateRepository;
import com.bs.odontograma.treatment.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

/**
 * Computes dashboard KPIs for the current tenant on the fly. Each query is a
 * COUNT/SUM, so the round-trip is small. If the dashboard becomes a hotspot,
 * cache the response per-tenant with Spring Cache.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TreatmentRepository treatmentRepository;
    private final EstimateRepository estimateRepository;
    private final ToothHistoryEntryRepository toothHistoryRepository;
    private final TenantContext tenantContext;

    public DashboardStatsResponse getStats() {
        UUID tenantId = tenantContext.getCurrentTenantId();

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        long totalPatients = patientRepository.countByTenantId(tenantId);
        long activePatients = patientRepository.countByTenantIdAndActiveTrue(tenantId);

        long appointmentsToday = appointmentRepository.countByTenantIdAndAppointmentDate(tenantId, today);
        long appointmentsThisWeek = appointmentRepository.countByTenantIdAndAppointmentDateBetween(
                tenantId, weekStart, weekEnd
        );
        long upcomingAppointments = appointmentRepository
                .countByTenantIdAndAppointmentDateGreaterThanEqualAndStatusIn(
                        tenantId, today,
                        List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
                );

        long treatmentsInCatalog = treatmentRepository.countByTenantId(tenantId);

        long pendingTreatmentItems =
                toothHistoryRepository.countByTenantIdAndTreatmentStatus(tenantId, TreatmentStatus.PENDING)
                        + toothHistoryRepository.countByTenantIdAndTreatmentStatus(tenantId, TreatmentStatus.IN_PROGRESS);

        long activeEstimates = estimateRepository.countByTenantIdAndStatusIn(
                tenantId, List.of(EstimateStatus.DRAFT, EstimateStatus.SENT, EstimateStatus.ACCEPTED)
        );
        BigDecimal estimatedRevenuePending = estimateRepository.sumTotalByTenantIdAndStatusIn(
                tenantId, List.of(EstimateStatus.SENT, EstimateStatus.ACCEPTED)
        );
        if (estimatedRevenuePending == null) estimatedRevenuePending = BigDecimal.ZERO;

        return DashboardStatsResponse.builder()
                .totalPatients(totalPatients)
                .activePatients(activePatients)
                .appointmentsToday(appointmentsToday)
                .appointmentsThisWeek(appointmentsThisWeek)
                .upcomingAppointments(upcomingAppointments)
                .treatmentsInCatalog(treatmentsInCatalog)
                .pendingTreatmentItems(pendingTreatmentItems)
                .activeEstimates(activeEstimates)
                .estimatedRevenuePending(estimatedRevenuePending)
                .build();
    }
}
