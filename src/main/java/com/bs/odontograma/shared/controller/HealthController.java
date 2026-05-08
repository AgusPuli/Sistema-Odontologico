package com.bs.odontograma.shared.controller;

import com.bs.odontograma.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para health checks de la aplicación.
 */
@RestController
@RequestMapping("/api/health")
@Slf4j
@Tag(name = "Health", description = "Endpoints de salud del sistema")
public class HealthController {

    @GetMapping
    @Operation(
            summary = "Health check",
            description = "Verifica que la aplicación esté funcionando correctamente"
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        log.debug("Health check requested");

        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "odontograma-backend");
        health.put("version", "1.0.0");

        return ResponseEntity.ok(ApiResponse.success(health));
    }

    @GetMapping("/ping")
    @Operation(
            summary = "Ping",
            description = "Endpoint simple para verificar conectividad"
    )
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}