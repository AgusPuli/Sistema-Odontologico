package com.bs.odontograma.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Aggregated KPIs for the current tenant. Computed on the fly so the dashboard
 * always reflects up-to-date data; if this becomes a hotspot, cache it per tenant.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalPatients;
    private long activePatients;
    private long appointmentsToday;
    private long appointmentsThisWeek;
    private long upcomingAppointments;
    private long treatmentsInCatalog;
    private long pendingTreatmentItems;
    private long activeEstimates;
    private BigDecimal estimatedRevenuePending;
}
