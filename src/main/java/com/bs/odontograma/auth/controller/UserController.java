package com.bs.odontograma.auth.controller;

import com.bs.odontograma.auth.dto.ResetPasswordRequest;
import com.bs.odontograma.auth.dto.UpdateUserRequest;
import com.bs.odontograma.auth.dto.UserResponse;
import com.bs.odontograma.auth.service.UserService;
import com.bs.odontograma.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for user management.
 * Only accessible by SUPERADMIN, ADMIN, and MANAGER roles.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'MANAGER')")
    @Operation(
            summary = "List all users",
            description = "Returns paginated list of users for current tenant with optional search"
    )
    public ResponseEntity<ApiResponse<Page<UserResponse>>> findAll(
            @Parameter(description = "Search term (email, first name, or last name)")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "firstName") Pageable pageable
    ) {
        log.debug("GET /api/users - Listing users with search: {}", search);

        Page<UserResponse> response = service.findAll(search, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> findById(
            @Parameter(description = "User ID") @PathVariable UUID id
    ) {
        log.debug("GET /api/users/{} - Finding user", id);

        UserResponse response = service.findById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'MANAGER')")
    @Operation(
            summary = "List active users",
            description = "Returns list of active users for current tenant"
    )
    public ResponseEntity<ApiResponse<List<UserResponse>>> findActive() {
        log.debug("GET /api/users/active - Finding active users");

        List<UserResponse> response = service.findActive();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/email-exists/{email}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'MANAGER')")
    @Operation(
            summary = "Check if email exists",
            description = "Returns true if email is already registered in the system"
    )
    public ResponseEntity<ApiResponse<Boolean>> existsByEmail(
            @Parameter(description = "Email to check") @PathVariable String email
    ) {
        log.debug("GET /api/users/email-exists/{} - Checking email", email);

        Boolean exists = service.existsByEmail(email);

        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Update user",
            description = "Updates user information (first name, last name, role). Email cannot be changed."
    )
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("PUT /api/users/{} - Updating user", id);

        UserResponse response = service.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "User updated successfully")
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Activate user")
    public ResponseEntity<ApiResponse<UserResponse>> activate(
            @Parameter(description = "User ID") @PathVariable UUID id
    ) {
        log.info("PATCH /api/users/{}/activate - Activating user", id);

        UserResponse response = service.activate(id);

        return ResponseEntity.ok(
                ApiResponse.success(response, "User activated successfully")
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Deactivate user")
    public ResponseEntity<ApiResponse<UserResponse>> deactivate(
            @Parameter(description = "User ID") @PathVariable UUID id
    ) {
        log.info("PATCH /api/users/{}/deactivate - Deactivating user", id);

        UserResponse response = service.deactivate(id);

        return ResponseEntity.ok(
                ApiResponse.success(response, "User deactivated successfully")
        );
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Reset user password",
            description = "Admin resets another user's password (SUPERADMIN and ADMIN only)"
    )
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        log.info("PATCH /api/users/{}/reset-password - Resetting password", id);

        service.resetPassword(id, request.getNewPassword());

        return ResponseEntity.ok(
                ApiResponse.success(null, "Password reset successfully")
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Delete user",
            description = "Soft deletes user by setting active to false (SUPERADMIN and ADMIN only)"
    )
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "User ID") @PathVariable UUID id
    ) {
        log.info("DELETE /api/users/{} - Deleting user", id);

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "User deleted successfully")
        );
    }
}
