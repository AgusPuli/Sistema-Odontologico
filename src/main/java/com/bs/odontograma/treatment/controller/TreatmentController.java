package com.bs.odontograma.treatment.controller;

import com.bs.odontograma.shared.dto.ApiResponse;
import com.bs.odontograma.shared.enums.DentalSpecialty;
import com.bs.odontograma.treatment.dto.request.CreateTreatmentRequest;
import com.bs.odontograma.treatment.dto.request.UpdateTreatmentRequest;
import com.bs.odontograma.treatment.dto.response.TreatmentResponse;
import com.bs.odontograma.treatment.service.TreatmentService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/treatments")
@RequiredArgsConstructor
@Tag(name = "Treatments", description = "Dental treatment catalog (per-tenant)")
public class TreatmentController {

    private final TreatmentService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    @Operation(summary = "Create treatment in catalog")
    public ResponseEntity<ApiResponse<TreatmentResponse>> create(@Valid @RequestBody CreateTreatmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Treatment created"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Search/list treatments")
    public ResponseEntity<ApiResponse<Page<TreatmentResponse>>> search(
            @RequestParam(required = false) DentalSpecialty specialty,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 50, sort = "specialty") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.search(specialty, search, pageable)));
    }

    @GetMapping("/by-specialty/{specialty}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "List active treatments for a given specialty")
    public ResponseEntity<ApiResponse<List<TreatmentResponse>>> findActiveBySpecialty(
            @PathVariable DentalSpecialty specialty
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.findActiveBySpecialty(specialty)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get treatment by ID")
    public ResponseEntity<ApiResponse<TreatmentResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    @Operation(summary = "Update treatment")
    public ResponseEntity<ApiResponse<TreatmentResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTreatmentRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Treatment updated"));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Treatment deactivated"));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable UUID id) {
        service.activate(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Treatment activated"));
    }

    @PostMapping("/seed-defaults")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    @Operation(summary = "Seed the default dental treatment catalog into the current tenant")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> seedDefaults() {
        int created = service.seedDefaults();
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("created", created),
                "Default catalog seeded"
        ));
    }
}
