package com.bs.odontograma.dashboard.controller;

import com.bs.odontograma.dashboard.dto.DashboardStatsResponse;
import com.bs.odontograma.dashboard.service.DashboardService;
import com.bs.odontograma.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Aggregated KPIs for the current tenant")
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get dashboard KPIs for the current tenant")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> stats() {
        return ResponseEntity.ok(ApiResponse.success(service.getStats()));
    }
}
