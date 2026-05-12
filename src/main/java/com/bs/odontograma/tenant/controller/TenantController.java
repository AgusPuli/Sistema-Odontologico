package com.bs.odontograma.tenant.controller;

import com.bs.odontograma.auth.dto.AuthResponse;
import com.bs.odontograma.auth.service.AuthService;
import com.bs.odontograma.shared.dto.ApiResponse;
import com.bs.odontograma.shared.security.UserPrincipal;
import com.bs.odontograma.tenant.dto.CreateTenantRequest;
import com.bs.odontograma.tenant.dto.RegisterTenantRequest;
import com.bs.odontograma.tenant.dto.TenantResponse;
import com.bs.odontograma.tenant.dto.UpdateTenantRequest;
import com.bs.odontograma.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tenants", description = "Tenant/Company management")
public class TenantController {

    private final TenantService service;
    private final AuthService authService;

    /**
     * Public self-service registration.
     * Creates a new tenant + its first ADMIN user in one atomic call and returns JWT tokens.
     * No authentication required — this is the SaaS onboarding entry point.
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register new tenant",
            description = "Self-service onboarding: creates a new tenant and its first ADMIN user, returns JWT tokens. No auth required."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterTenantRequest request
    ) {
        log.info("Tenant self-registration: clinic='{}'", request.getClinicName());
        AuthResponse response = authService.registerTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tenant registered successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Create tenant", description = "Creates a new tenant/company (SUPERADMIN only)")
    public ResponseEntity<ApiResponse<TenantResponse>> create(
            @Valid @RequestBody CreateTenantRequest request
    ) {
        TenantResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tenant created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Find tenant by ID")
    public ResponseEntity<ApiResponse<TenantResponse>> findById(
            @PathVariable UUID id
    ) {
        TenantResponse response = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "List all tenants")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> findAll() {
        List<TenantResponse> response = service.findAll();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "List active tenants")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> findActive() {
        List<TenantResponse> response = service.findActive();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update tenant", description = "MANAGER cannot modify tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantRequest request
    ) {
        TenantResponse response = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tenant updated"));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Deactivate tenant")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Tenant deactivated"));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Activate tenant")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable UUID id) {
        service.activate(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Tenant activated"));
    }
    // BACKEND - Agregar a TenantController.java

    /**
     * Get current user's tenant information.
     * Any authenticated user can access their own tenant info.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current tenant", description = "Returns the tenant of the authenticated user")
    public ResponseEntity<ApiResponse<TenantResponse>> getCurrentTenant() {
        // Get tenantId from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UUID tenantId = userPrincipal.getTenantId();

        TenantResponse response = service.findById(tenantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}