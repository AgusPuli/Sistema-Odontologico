package com.bs.odontograma.periodontogram.controller;

import com.bs.odontograma.periodontogram.dto.request.CreatePeriodontogramRequest;
import com.bs.odontograma.periodontogram.dto.request.UpdatePeriodontalToothRequest;
import com.bs.odontograma.periodontogram.dto.response.PeriodontogramResponse;
import com.bs.odontograma.periodontogram.service.PeriodontogramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/periodontograms")
@RequiredArgsConstructor
@Tag(name = "Periodontograms", description = "Periodontal exam charts")
public class PeriodontogramController {

    private final PeriodontogramService service;

    /**
     * Idempotent: returns existing current chart or creates a fresh one with all
     * 32 permanent teeth pre-seeded (null measurements, ready to fill).
     */
    @Operation(summary = "Get or create the current periodontogram for a patient")
    @PostMapping("/get-or-create-current")
    public ResponseEntity<PeriodontogramResponse> getOrCreateCurrent(
            @Valid @RequestBody CreatePeriodontogramRequest request) {
        return ResponseEntity.ok(service.getOrCreateCurrent(request));
    }

    /**
     * Force-creates a new periodontogram (archives the current one if any).
     * Useful for starting a new exam cycle for an existing patient.
     */
    @Operation(summary = "Create a new periodontogram (archives the current one)")
    @PostMapping
    public ResponseEntity<PeriodontogramResponse> create(
            @Valid @RequestBody CreatePeriodontogramRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Get a periodontogram by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PeriodontogramResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Get the current periodontogram for a patient")
    @GetMapping("/patient/{patientId}/current")
    public ResponseEntity<PeriodontogramResponse> findCurrentForPatient(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(service.findCurrentForPatient(patientId));
    }

    @Operation(summary = "Get all periodontograms for a patient (history), newest first")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PeriodontogramResponse>> findAllForPatient(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(service.findAllForPatient(patientId));
    }

    /**
     * Patch a single tooth within a periodontogram.
     * Accepts partial updates: only non-null fields are written.
     * Also accepts a {@code sites} list to update individual site measurements.
     * Bleeding and plaque indices are recomputed automatically on every save.
     */
    @Operation(summary = "Update a tooth's measurements in a periodontogram")
    @PutMapping("/{id}/teeth/{fdiNumber}")
    public ResponseEntity<PeriodontogramResponse> updateTooth(
            @PathVariable UUID id,
            @PathVariable Integer fdiNumber,
            @Valid @RequestBody UpdatePeriodontalToothRequest request) {
        return ResponseEntity.ok(service.updateTooth(id, fdiNumber, request));
    }
}
