package com.bs.odontograma.auth.service;

import com.bs.odontograma.auth.dto.UpdateUserRequest;
import com.bs.odontograma.auth.dto.UserResponse;
import com.bs.odontograma.auth.entity.User;
import com.bs.odontograma.auth.mapper.UserMapper;
import com.bs.odontograma.auth.repository.UserRepository;
import com.bs.odontograma.shared.exception.BusinessRuleViolationException;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for user management.
 * Handles CRUD operations for users within a tenant.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final TenantContext tenantContext;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(String search, Pageable pageable) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        log.debug("Finding users for tenant: {} with search: {}", tenantId, search);

        Page<User> users;
        if (search != null && !search.isBlank()) {
            users = repository.searchByTenantId(tenantId, search.trim(), pageable);
        } else {
            users = repository.findByTenantId(tenantId, pageable);
        }

        return users.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        log.debug("Finding user: {} for tenant: {}", id, tenantId);

        User user = repository.findById(id)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        return mapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findActive() {
        UUID tenantId = tenantContext.getCurrentTenantId();

        log.debug("Finding active users for tenant: {}", tenantId);

        List<User> users = repository.findByTenantIdAndActiveTrue(tenantId);

        return users.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public UserResponse update(UUID id, UpdateUserRequest request) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        log.info("Updating user: {} for tenant: {}", id, tenantId);

        User user = repository.findById(id)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.updateFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.updateLastName(request.getLastName());
        }

        if (request.getRole() != null) {
            user.updateRole(request.getRole());
        }

        user = repository.save(user);

        log.info("User updated successfully: {}", id);

        return mapper.toResponse(user);
    }

    public UserResponse activate(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        log.info("Activating user: {} for tenant: {}", id, tenantId);

        User user = repository.findById(id)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        user.activate();
        user = repository.save(user);

        log.info("User activated successfully: {}", id);

        return mapper.toResponse(user);
    }

    public UserResponse deactivate(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        log.info("Deactivating user: {} for tenant: {}", id, tenantId);

        User user = repository.findById(id)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        user.deactivate();
        user = repository.save(user);

        log.info("User deactivated successfully: {}", id);

        return mapper.toResponse(user);
    }

    public void resetPassword(UUID id, String newPassword) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        log.info("Resetting password for user: {} by admin", id);

        User user = repository.findById(id)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        if (newPassword == null || newPassword.length() < 8) {
            throw new BusinessRuleViolationException(
                    "Password must be at least 8 characters"
            );
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        repository.save(user);

        log.info("Password reset successfully for user: {}", id);
    }

    public void delete(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();

        log.info("Deleting user: {} for tenant: {}", id, tenantId);

        User user = repository.findById(id)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        user.deactivate();
        repository.save(user);

        log.info("User deleted successfully: {}", id);
    }
}
