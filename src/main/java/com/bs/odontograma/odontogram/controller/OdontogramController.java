package com.bs.odontograma.odontogram.controller;

import com.bs.odontograma.odontogram.dto.request.CreateOdontogramRequest;
import com.bs.odontograma.odontogram.dto.request.UpdateToothRequest;
import com.bs.odontograma.odontogram.dto.response.OdontogramResponse;
import com.bs.odontograma.odontogram.dto.response.ToothHistoryResponse;
import com.bs.odontograma.odontogram.enums.TreatmentStatus;
import com.bs.odontograma.odontogram.service.OdontogramService;
import com.bs.odontograma.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/odontograms")
@RequiredArgsConstructor
@Tag(name = "Odontograms", description = "Patient odontogram (dental chart) management")
public class OdontogramController {

    private final OdontogramService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Create new odontogram for a patient (auto-marks previous as historical)")
    public ResponseEntity<ApiResponse<OdontogramResponse>> create(
            @Valid @RequestBody CreateOdontogramRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Odontogram created"));
    }

    @PostMapping("/get-or-create-current")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(
            summary = "Get-or-create current odontogram (idempotent)",
            description = "Returns the current odontogram if it exists, otherwise creates one. " +
                    "Safe to call on chart load without accidentally archiving the patient's data."
    )
    public ResponseEntity<ApiResponse<OdontogramResponse>> getOrCreateCurrent(
            @Valid @RequestBody CreateOdontogramRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getOrCreateCurrent(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get odontogram by ID")
    public ResponseEntity<ApiResponse<OdontogramResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
    }

    @GetMapping("/patient/{patientId}/current")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get current odontogram for a patient")
    public ResponseEntity<ApiResponse<OdontogramResponse>> findCurrent(@PathVariable UUID patientId) {
        return ResponseEntity.ok(ApiResponse.success(service.findCurrentForPatient(patientId)));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get full odontogram history for a patient")
    public ResponseEntity<ApiResponse<List<OdontogramResponse>>> findAllForPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(ApiResponse.success(service.findAllForPatient(patientId)));
    }

    @PutMapping("/{id}/teeth/{fdiNumber}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Update a single tooth (whole-tooth condition + per-surface findings + auto-history entry)")
    public ResponseEntity<ApiResponse<OdontogramResponse>> updateTooth(
            @PathVariable UUID id,
            @PathVariable Integer fdiNumber,
            @Valid @RequestBody UpdateToothRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(service.updateTooth(id, fdiNumber, request), "Tooth updated")
        );
    }

    @GetMapping("/{id}/teeth/{fdiNumber}/history")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get clinical history of a single tooth")
    public ResponseEntity<ApiResponse<List<ToothHistoryResponse>>> getToothHistory(
            @PathVariable UUID id,
            @PathVariable Integer fdiNumber
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getToothHistory(id, fdiNumber)));
    }

    @GetMapping("/{id}/treatment-plan")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Treatment plan: history entries across all teeth (optionally filtered by status)")
    public ResponseEntity<ApiResponse<List<ToothHistoryResponse>>> getTreatmentPlan(
            @PathVariable UUID id,
            @RequestParam(required = false) TreatmentStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getTreatmentPlan(id, status)));
    }
}
