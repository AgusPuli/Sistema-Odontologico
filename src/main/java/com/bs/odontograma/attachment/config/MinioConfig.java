package com.bs.odontograma.attachment.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the MinIO client bean and ensures the bucket exists on startup.
 * Failure here is logged but does not abort boot — the app still runs, attempts
 * to upload will fail with a clear error and developers can fix MinIO out of
 * band (typical local-dev scenario where MinIO container isn't running yet).
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MinioConfig {

    private final MinioProperties properties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }

    @PostConstruct
    public void ensureBucketExists() {
        try {
            MinioClient client = minioClient();
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(properties.getBucket()).build()
            );
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
                log.info("Created MinIO bucket: {}", properties.getBucket());
            } else {
                log.info("MinIO bucket already present: {}", properties.getBucket());
            }
        } catch (Exception e) {
            log.warn("Could not verify/create MinIO bucket '{}': {}. Uploads will fail until MinIO is reachable.",
                    properties.getBucket(), e.getMessage());
        }
    }
}
