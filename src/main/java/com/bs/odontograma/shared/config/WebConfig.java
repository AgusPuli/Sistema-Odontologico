package com.bs.odontograma.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Web configuration including CORS settings for frontend communication.
 *
 * CORS Configuration:
 * - Supports wildcard subdomain patterns (e.g., https://*.artesvirtualesgroup.com)
 * - Configurable via CORS_ALLOWED_ORIGINS environment variable
 * - Defaults to localhost for development
 *
 * Environment Variable Format (comma-separated, no spaces):
 * CORS_ALLOWED_ORIGINS=https://artesvirtualesgroup.com,https://*.artesvirtualesgroup.com
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    @Value("${arca.connection.timeout-seconds:30}")
    private int arcaTimeoutSeconds;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Parse comma-separated origins from environment variable
        List<String> originsList = Arrays.asList(allowedOrigins.split(","));

        // Use allowedOriginPatterns to support wildcards (e.g., https://*.example.com)
        // This is required for subdomain wildcards to work properly
        configuration.setAllowedOriginPatterns(originsList);

        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Allowed headers (* allows all headers)
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Tenant-ID",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Exposed headers (headers that frontend can read from response)
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Tenant-ID"
        ));

        // Allow credentials (cookies, authorization headers)
        // IMPORTANT: When allowCredentials is true, allowedOriginPatterns cannot be "*"
        configuration.setAllowCredentials(true);

        // Max age for preflight requests (1 hour)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(arcaTimeoutSeconds))
                .setReadTimeout(Duration.ofSeconds(arcaTimeoutSeconds))
                .build();
    }
}