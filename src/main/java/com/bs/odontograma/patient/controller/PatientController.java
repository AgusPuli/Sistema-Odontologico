package com.bs.odontograma.patient.controller;

import com.bs.odontograma.patient.dto.request.CreatePatientRequest;
import com.bs.odontograma.patient.dto.request.UpdatePatientRequest;
import com.bs.odontograma.patient.dto.response.PatientResponse;
import com.bs.odontograma.patient.service.PatientService;
import com.bs.odontograma.shared.dto.ApiResponse;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Dental patient management")
public class PatientController {

    private final PatientService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Create patient")
    public ResponseEntity<ApiResponse<PatientResponse>> create(@Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Patient created"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "List patients (paginated, with search)")
    public ResponseEntity<ApiResponse<Page<PatientResponse>>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.findAll(search, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get patient by ID")
    public ResponseEntity<ApiResponse<PatientResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Update patient")
    public ResponseEntity<ApiResponse<PatientResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Patient updated"));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Deactivate patient (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Patient deactivated"));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Activate patient")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable UUID id) {
        service.activate(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Patient activated"));
    }
}
