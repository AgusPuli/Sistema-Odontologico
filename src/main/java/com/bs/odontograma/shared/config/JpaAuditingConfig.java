package com.bs.odontograma.shared.config;

import com.bs.odontograma.shared.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Auditing configuration.
 * Enables automatic population of audit fields.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    /**
     * Provides the current user for auditing purposes.
     * If no authenticated user is present, returns "SYSTEM".
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }

            Object principal = authentication.getPrincipal();

            // If it is our custom UserPrincipal
            if (principal instanceof UserPrincipal) {
                return Optional.of(((UserPrincipal) principal).getEmail());
            }

            // If it is a String (typically the username)
            if (principal instanceof String) {
                String username = (String) principal;
                // Avoid "anonymousUser" set by Spring Security
                if (!"anonymousUser".equals(username)) {
                    return Optional.of(username);
                }
            }

            return Optional.of("SYSTEM");
        };
    }
}
