package com.bs.odontograma.clinicalsession.controller;

import com.bs.odontograma.clinicalsession.dto.request.CreateClinicalSessionRequest;
import com.bs.odontograma.clinicalsession.dto.request.UpdateClinicalSessionRequest;
import com.bs.odontograma.clinicalsession.dto.response.ClinicalSessionResponse;
import com.bs.odontograma.clinicalsession.service.ClinicalSessionService;
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

/**
 * Clinical sessions (evolución por turno). The record of what actually
 * happened at a visit — SOAP notes, procedures performed, anesthesia, vitals.
 */
@RestController
@RequestMapping("/api/clinical-sessions")
@RequiredArgsConstructor
@Tag(name = "Clinical sessions", description = "Per-visit clinical record (SOAP + procedures)")
public class ClinicalSessionController {

    private final ClinicalSessionService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Create a new clinical session (defaults to DRAFT)")
    public ResponseEntity<ApiResponse<ClinicalSessionResponse>> create(
            @Valid @RequestBody CreateClinicalSessionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Clinical session created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get a clinical session by ID")
    public ResponseEntity<ApiResponse<ClinicalSessionResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "List clinical sessions for a patient (most recent first)")
    public ResponseEntity<ApiResponse<List<ClinicalSessionResponse>>> byPatient(
            @PathVariable UUID patientId
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.findByPatient(patientId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Update a clinical session (partial, also replaces procedures when supplied)")
    public ResponseEntity<ApiResponse<ClinicalSessionResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClinicalSessionRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(service.update(id, request), "Clinical session updated")
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    @Operation(summary = "Delete a clinical session")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Clinical session deleted"));
    }
}
