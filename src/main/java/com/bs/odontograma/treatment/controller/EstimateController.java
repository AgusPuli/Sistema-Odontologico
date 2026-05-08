package com.bs.odontograma.treatment.controller;

import com.bs.odontograma.shared.dto.ApiResponse;
import com.bs.odontograma.treatment.dto.request.CreateEstimateRequest;
import com.bs.odontograma.treatment.dto.response.EstimateResponse;
import com.bs.odontograma.treatment.enums.EstimateStatus;
import com.bs.odontograma.treatment.service.EstimateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estimates")
@RequiredArgsConstructor
@Tag(name = "Estimates", description = "Patient estimates / presupuestos")
public class EstimateController {

    private final EstimateService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Create estimate (presupuesto) for a patient")
    public ResponseEntity<ApiResponse<EstimateResponse>> create(@Valid @RequestBody CreateEstimateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Estimate created"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "List estimates")
    public ResponseEntity<ApiResponse<Page<EstimateResponse>>> findAll(
            @PageableDefault(size = 20, sort = "issueDate") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.findAll(pageable)));
    }

    @GetMapping("/by-patient/{patientId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "List estimates of a patient")
    public ResponseEntity<ApiResponse<List<EstimateResponse>>> findByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(ApiResponse.success(service.findByPatient(patientId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get estimate by ID")
    public ResponseEntity<ApiResponse<EstimateResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Change estimate status (DRAFT/SENT/ACCEPTED/REJECTED/EXPIRED/CANCELLED)")
    public ResponseEntity<ApiResponse<EstimateResponse>> changeStatus(
            @PathVariable UUID id,
            @RequestParam EstimateStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.changeStatus(id, status), "Status changed"));
    }
}
