package com.bs.odontograma.appointment.controller;

import com.bs.odontograma.appointment.dto.request.ChangeStatusRequest;
import com.bs.odontograma.appointment.dto.request.CreateAppointmentRequest;
import com.bs.odontograma.appointment.dto.request.RescheduleRequest;
import com.bs.odontograma.appointment.dto.request.UpdateAppointmentRequest;
import com.bs.odontograma.appointment.dto.response.AppointmentResponse;
import com.bs.odontograma.appointment.service.AppointmentService;
import com.bs.odontograma.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Patient appointment management")
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Create appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(
            @Valid @RequestBody CreateAppointmentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Appointment created"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "List appointments (paginated)")
    public ResponseEntity<ApiResponse<Page<AppointmentResponse>>> findAll(
            @PageableDefault(size = 20, sort = "appointmentDate") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.findAll(pageable)));
    }

    @GetMapping("/by-date")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "List appointments for a specific day")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> findByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.findByDate(date)));
    }

    @GetMapping("/by-range")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "List appointments for a date range (calendar view)")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> findByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.findByDateRange(from, to)));
    }

    @GetMapping("/by-patient/{patientId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "List appointments of a patient")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> findByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(ApiResponse.success(service.findByPatient(patientId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<ApiResponse<AppointmentResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Update appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAppointmentRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Appointment updated"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Change appointment status (validates state machine)")
    public ResponseEntity<ApiResponse<AppointmentResponse>> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                service.changeStatus(id, request.getStatus()), "Status changed"));
    }

    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Reschedule appointment to a new date/time")
    public ResponseEntity<ApiResponse<AppointmentResponse>> reschedule(
            @PathVariable UUID id,
            @Valid @RequestBody RescheduleRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                service.reschedule(id, request), "Appointment rescheduled"));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Delete appointment")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Appointment deleted"));
    }
}
