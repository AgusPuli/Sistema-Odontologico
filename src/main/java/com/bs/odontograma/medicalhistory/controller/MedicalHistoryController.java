package com.bs.odontograma.medicalhistory.controller;

import com.bs.odontograma.medicalhistory.dto.request.MedicalHistoryRequest;
import com.bs.odontograma.medicalhistory.dto.response.MedicalHistoryResponse;
import com.bs.odontograma.medicalhistory.service.MedicalHistoryService;
import com.bs.odontograma.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Patient-scoped medical history (anamnesis). Mounted under /api/patients/{id}
 * because there is exactly one history per patient.
 *
 *   GET /api/patients/{patientId}/medical-history   → read (returns empty stub if none)
 *   PUT /api/patients/{patientId}/medical-history   → upsert (partial — non-null wins)
 */
@RestController
@RequestMapping("/api/patients/{patientId}/medical-history")
@RequiredArgsConstructor
@Tag(name = "Medical history", description = "Structured medical history (anamnesis) per patient")
public class MedicalHistoryController {

    private final MedicalHistoryService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get patient's medical history")
    public ResponseEntity<ApiResponse<MedicalHistoryResponse>> get(@PathVariable UUID patientId) {
        return ResponseEntity.ok(ApiResponse.success(service.findByPatient(patientId)));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Upsert patient's medical history (partial update)")
    public ResponseEntity<ApiResponse<MedicalHistoryResponse>> upsert(
            @PathVariable UUID patientId,
            @Valid @RequestBody MedicalHistoryRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(service.upsert(patientId, request), "Medical history saved")
        );
    }
}
