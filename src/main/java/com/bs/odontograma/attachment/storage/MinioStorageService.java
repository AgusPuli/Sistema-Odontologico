package com.bs.odontograma.attachment.storage;

import com.bs.odontograma.attachment.config.MinioProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * MinIO/S3-compatible implementation of {@link StorageService}.
 * Single bucket per installation. Keys are namespaced by tenant id so a misrouted
 * read from another tenant would still need a valid presigned URL.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MinioStorageService implements StorageService {

    private final MinioClient client;
    private final MinioProperties props;

    @Override
    public String upload(String storageKey, InputStream content, long sizeBytes, String contentType) {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(props.getBucket())
                    .object(storageKey)
                    .stream(content, sizeBytes, -1)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .build());
            log.debug("Uploaded {} ({} bytes) to bucket {}", storageKey, sizeBytes, props.getBucket());
            return storageKey;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload to MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public String presignedGetUrl(String storageKey, Duration ttl) {
        try {
            return client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(props.getBucket())
                            .object(storageKey)
                            .expiry((int) ttl.getSeconds(), TimeUnit.SECONDS)
                            .build());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to presign GET URL: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(props.getBucket())
                    .object(storageKey)
                    .build());
            log.debug("Deleted {} from bucket {}", storageKey, props.getBucket());
        } catch (Exception e) {
            // Don't blow up the delete flow if the object was already missing
            log.warn("Failed to remove {} from MinIO: {}", storageKey, e.getMessage());
        }
    }
}
