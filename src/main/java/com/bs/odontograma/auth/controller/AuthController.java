package com.bs.odontograma.auth.controller;

import com.bs.odontograma.auth.dto.*;
import com.bs.odontograma.auth.service.AuthService;
import com.bs.odontograma.shared.dto.ApiResponse;
import com.bs.odontograma.shared.security.UserPrincipal;
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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("Login attempt for: {}", request.getEmail());

        AuthResponse response = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                ApiResponse.success(response, "Login successful")
        );
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Register user", description = "Registers a new user in the system (SUPERADMIN and ADMIN only)")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("Registration attempt for: {}", request.getEmail());

        authService.registerUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getRole(),
                request.getTenantIdAsUuid()
        );

        AuthResponse response = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Renews the access token using a refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestParam String refreshToken
    ) {
        log.info("Refresh token attempt");

        AuthResponse response = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Token refreshed")
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logs out the current user")
    public ResponseEntity<ApiResponse<Void>> logout() {
        log.info("Logout request");
        return ResponseEntity.ok(
                ApiResponse.success(null, "Logout successful")
        );
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "User changes their own password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        authService.changePassword(
                userPrincipal.getId(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                ApiResponse.success(null, "Password updated successfully")
        );
    }
}
