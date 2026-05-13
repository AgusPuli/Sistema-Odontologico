package com.bs.odontograma.attachment.service;

import com.bs.odontograma.attachment.dto.request.UploadAttachmentRequest;
import com.bs.odontograma.attachment.dto.response.AttachmentResponse;
import com.bs.odontograma.attachment.entity.Attachment;
import com.bs.odontograma.attachment.enums.AttachmentOwnerType;
import com.bs.odontograma.attachment.mapper.AttachmentMapper;
import com.bs.odontograma.attachment.repository.AttachmentRepository;
import com.bs.odontograma.attachment.storage.StorageService;
import com.bs.odontograma.shared.exception.EntityNotFoundException;
import com.bs.odontograma.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AttachmentService {

    /** Presigned URLs valid for 1 hour — long enough for a page session, short enough to limit replay. */
    private static final Duration URL_TTL = Duration.ofHours(1);

    private final AttachmentRepository repository;
    private final AttachmentMapper mapper;
    private final StorageService storage;
    private final TenantContext tenantContext;

    public AttachmentResponse upload(MultipartFile file, UploadAttachmentRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        UUID tenantId = tenantContext.getCurrentTenantId();

        String safeName = sanitizeFileName(Objects.requireNonNullElse(file.getOriginalFilename(), "file"));
        String storageKey = buildStorageKey(tenantId, request.getOwnerType(), request.getOwnerId(), safeName);

        // Push bytes to object storage first; on DB failure the orphan is cleaned up by the catch.
        storage.upload(storageKey, file.getInputStream(), file.getSize(), file.getContentType());

        try {
            Attachment entity = Attachment.builder()
                    .ownerType(request.getOwnerType())
                    .ownerId(request.getOwnerId())
                    .toothFdi(request.getToothFdi())
                    .sessionId(request.getSessionId())
                    .category(request.getCategory())
                    .fileName(safeName)
                    .storageKey(storageKey)
                    .contentType(file.getContentType())
                    .sizeBytes(file.getSize())
                    .description(request.getDescription())
                    .takenAt(request.getTakenAt())
                    .build();
            entity.setTenantId(tenantId);
            entity = repository.save(entity);

            log.info("Uploaded attachment {} ({}, {} bytes) for {} {}",
                    entity.getId(), request.getCategory(), file.getSize(),
                    request.getOwnerType(), request.getOwnerId());

            return enrichWithUrl(entity);
        } catch (RuntimeException ex) {
            // Avoid orphan blobs in MinIO when the DB row fails to save.
            storage.delete(storageKey);
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> list(AttachmentOwnerType ownerType, UUID ownerId) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        return repository
                .findByTenantIdAndOwnerTypeAndOwnerIdOrderByCreatedAtDesc(tenantId, ownerType, ownerId)
                .stream()
                .map(this::enrichWithUrl)
                .toList();
    }

    @Transactional(readOnly = true)
    public AttachmentResponse findById(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Attachment entity = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Attachment", id));
        return enrichWithUrl(entity);
    }

    public void delete(UUID id) {
        UUID tenantId = tenantContext.getCurrentTenantId();
        Attachment entity = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Attachment", id));
        storage.delete(entity.getStorageKey());
        repository.delete(entity);
        log.info("Deleted attachment {} (storage key {})", id, entity.getStorageKey());
    }

    // ---------- helpers ----------

    private AttachmentResponse enrichWithUrl(Attachment entity) {
        AttachmentResponse r = mapper.toResponse(entity);
        r.setDownloadUrl(storage.presignedGetUrl(entity.getStorageKey(), URL_TTL));
        return r;
    }

    /**
     * Storage key layout:
     *   tenants/{tenantId}/{ownerType}/{ownerId}/{uuid}-{filename}
     * Including a UUID guarantees uniqueness even when the same filename is uploaded twice.
     */
    private String buildStorageKey(UUID tenantId, AttachmentOwnerType ownerType, UUID ownerId, String safeName) {
        return "tenants/" + tenantId
                + "/" + ownerType.name().toLowerCase()
                + "/" + ownerId
                + "/" + UUID.randomUUID() + "-" + safeName;
    }

    private static String sanitizeFileName(String name) {
        // Strip path separators and bracket chars — anything URL-unsafe gets `_`.
        return name.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
