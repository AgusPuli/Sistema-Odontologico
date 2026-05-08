package com.bs.odontograma.audit.config;

import com.bs.odontograma.audit.listener.AuditListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class to inject Spring ApplicationContext into JPA AuditListener.
 * This is necessary because JPA entity listeners are not managed by Spring
 * and cannot use dependency injection directly.
 */
@Configuration
@RequiredArgsConstructor
public class AuditListenerConfig {

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        AuditListener.setApplicationContext(applicationContext);
    }
}
