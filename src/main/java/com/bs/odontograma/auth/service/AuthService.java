package com.bs.odontograma.auth.service;

import com.bs.odontograma.auth.dto.AuthResponse;
import com.bs.odontograma.auth.dto.UserResponse;
import com.bs.odontograma.auth.entity.User;
import com.bs.odontograma.auth.entity.UserRole;
import com.bs.odontograma.auth.mapper.UserMapper;
import com.bs.odontograma.auth.repository.UserRepository;
import com.bs.odontograma.shared.exception.BusinessRuleViolationException;
import com.bs.odontograma.shared.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
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
