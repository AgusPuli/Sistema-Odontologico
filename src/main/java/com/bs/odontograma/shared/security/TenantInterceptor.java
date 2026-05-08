package com.bs.odontograma.shared.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * Interceptor that captures the X-Tenant-ID header and stores it in TenantContext.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private final TenantContext tenantContext;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId != null && !tenantId.isBlank()) {
            try {
                UUID tenantUuid = UUID.fromString(tenantId);
                tenantContext.setCurrentTenantId(tenantUuid);
                log.debug("Tenant ID set: {}", tenantUuid);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid tenant ID format: {}", tenantId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }
        } else {
            log.debug("No tenant ID provided in request");
        }

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        tenantContext.clear();
    }
}
