package com.bs.odontograma.auth.service;

import com.bs.odontograma.auth.dto.AuthResponse;
import com.bs.odontograma.auth.dto.BootstrapRequest;
import com.bs.odontograma.auth.dto.UserResponse;
import com.bs.odontograma.tenant.dto.RegisterTenantRequest;
import com.bs.odontograma.auth.entity.User;
import com.bs.odontograma.auth.entity.UserRole;
import com.bs.odontograma.auth.mapper.UserMapper;
import com.bs.odontograma.auth.repository.UserRepository;
import com.bs.odontograma.shared.exception.BusinessRuleViolationException;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.jwt.JwtTokenProvider;
import com.bs.odontograma.tenant.entity.SubscriptionPlan;
import com.bs.odontograma.tenant.entity.Tenant;
import com.bs.odontograma.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    public AuthResponse login(String email, String password) {
        log.info("Attempting login for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.isActive()) {
            log.warn("Inactive user attempted login: {}", email);
            throw new BadCredentialsException("User account is disabled");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Invalid password attempt for user: {}", email);
            throw new BadCredentialsException("Invalid credentials");
        }

        String accessToken = tokenProvider.generateAccessToken(
                user.getId(),
                user.getTenantId(),
                user.getEmail(),
                user.getRole()
        );

        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        log.info("Login successful for user: {} (tenant: {})", email, user.getTenantId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(userMapper.toResponse(user))
                .build();
    }

    public UserResponse registerUser(
            String email,
            String password,
            String firstName,
            String lastName,
            UserRole role,
            UUID tenantId
    ) {
        log.info("Registering new user: {} in tenant: {}", email, tenantId);

        if (userRepository.existsByEmail(email)) {
            throw new BusinessRuleViolationException(
                    "A user with this email already exists: " + email
            );
        }

        if (password == null || password.length() < 8) {
            throw new BusinessRuleViolationException(
                    "Password must be at least 8 characters"
            );
        }

        User user = User.builder()
                .email(email.toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .role(role != null ? role : UserRole.MANAGER)
                .active(true)
                .build();

        user.setTenantId(tenantId);
        user = userRepository.save(user);

        log.info("User registered successfully: {} (tenant: {})", email, tenantId);

        return userMapper.toResponse(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        UUID userId = tokenProvider.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!user.isActive()) {
            throw new BadCredentialsException("User account is disabled");
        }

        String newAccessToken = tokenProvider.generateAccessToken(
                user.getId(),
                user.getTenantId(),
                user.getEmail(),
                user.getRole()
        );

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(userMapper.toResponse(user))
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse findCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        return userMapper.toResponse(user);
    }

    /**
     * Creates the first tenant + SUPERADMIN of the installation. Idempotency guard:
     * fails with 409 if any tenant already exists, so this endpoint is safe to leave
     * exposed but useless after the first successful call.
     */
    public AuthResponse bootstrap(BootstrapRequest request) {
        long existing = tenantRepository.count();
        if (existing > 0) {
            throw new BusinessRuleViolationException(
                    "System already initialized — bootstrap endpoint is disabled"
            );
        }

        log.info("Bootstrapping installation with admin {} and clinic '{}'",
                request.getAdminEmail(), request.getClinicName());

        // Create tenant on the SUPER plan so user limits don't get in the way
        Tenant tenant = Tenant.builder()
                .name(request.getClinicName())
                .email(request.getAdminEmail())
                .subscriptionPlan(SubscriptionPlan.SUPER)
                .maxUsers(SubscriptionPlan.SUPER.getMaxUsers())
                .active(true)
                .planExpiration(LocalDateTime.now().plusYears(10))
                .build();
        tenant = tenantRepository.save(tenant);

        // Create the SUPERADMIN user owned by that tenant
        User admin = User.builder()
                .email(request.getAdminEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getAdminPassword()))
                .firstName(request.getAdminFirstName().trim())
                .lastName(request.getAdminLastName())
                .role(UserRole.SUPERADMIN)
                .active(true)
                .build();
        admin.setTenantId(tenant.getId());
        admin = userRepository.save(admin);

        log.info("Bootstrap done. Tenant {}, user {}", tenant.getId(), admin.getId());

        // Issue tokens immediately so the front can navigate straight to the dashboard
        String accessToken = tokenProvider.generateAccessToken(
                admin.getId(), admin.getTenantId(), admin.getEmail(), admin.getRole()
        );
        String refreshToken = tokenProvider.generateRefreshToken(admin.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(userMapper.toResponse(admin))
                .build();
    }

    /**
     * Self-service tenant registration (no auth required).
     * Creates a new Tenant + the first ADMIN user for that tenant atomically,
     * then returns JWT tokens so the caller can navigate straight to the dashboard.
     */
    public AuthResponse registerTenant(RegisterTenantRequest request) {
        log.info("Self-service tenant registration: clinic='{}', email='{}'",
                request.getClinicName(), request.getAdminEmail());

        // Guard: email must be unique across ALL users
        if (userRepository.existsByEmail(request.getAdminEmail())) {
            throw new BusinessRuleViolationException(
                    "A user with email " + request.getAdminEmail() + " already exists"
            );
        }

        // Guard: taxId must be unique if supplied
        if (request.getTaxId() != null && !request.getTaxId().isBlank()
                && tenantRepository.existsByTaxId(request.getTaxId())) {
            throw new BusinessRuleViolationException(
                    "A tenant with tax ID " + request.getTaxId() + " already exists"
            );
        }

        // Create the tenant
        Tenant tenant = Tenant.builder()
                .name(request.getClinicName().trim())
                .email(request.getAdminEmail().toLowerCase().trim())
                .taxId(request.getTaxId())
                .phone(request.getPhone())
                .subscriptionPlan(request.getPlan())
                .maxUsers(request.getPlan().getMaxUsers())
                .active(true)
                .planExpiration(LocalDateTime.now().plusYears(1))
                .build();
        tenant = tenantRepository.save(tenant);

        // Create the first ADMIN for this tenant
        User admin = User.builder()
                .email(request.getAdminEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getAdminPassword()))
                .firstName(request.getAdminFirstName().trim())
                .lastName(request.getAdminLastName().trim())
                .role(UserRole.ADMIN)
                .active(true)
                .build();
        admin.setTenantId(tenant.getId());
        admin = userRepository.save(admin);

        log.info("Tenant registration complete. Tenant={}, user={}", tenant.getId(), admin.getId());

        // Issue tokens immediately
        String accessToken = tokenProvider.generateAccessToken(
                admin.getId(), admin.getTenantId(), admin.getEmail(), admin.getRole()
        );
        String refreshToken = tokenProvider.generateRefreshToken(admin.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(userMapper.toResponse(admin))
                .build();
    }

    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new BusinessRuleViolationException(
                    "New password must be different from the current password"
            );
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", userId);
    }
}
