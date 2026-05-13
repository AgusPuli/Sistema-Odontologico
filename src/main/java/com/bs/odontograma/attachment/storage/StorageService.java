package com.bs.odontograma.attachment.storage;

import java.io.InputStream;
import java.time.Duration;

/**
 * Generic object-storage abstraction. Today implemented by {@link MinioStorageService}
 * but the contract is intentionally provider-agnostic so we can swap to S3, GCS or a
 * filesystem-backed dev implementation without touching the attachment module.
 */
public interface StorageService {

    /** Upload bytes under {@code storageKey}. Returns the same key for chaining. */
    String upload(String storageKey, InputStream content, long sizeBytes, String contentType);

    /** Generate a time-limited GET URL the frontend can fetch directly. */
    String presignedGetUrl(String storageKey, Duration ttl);

    /** Permanently delete the object. No-op if the key does not exist. */
    void delete(String storageKey);
}
