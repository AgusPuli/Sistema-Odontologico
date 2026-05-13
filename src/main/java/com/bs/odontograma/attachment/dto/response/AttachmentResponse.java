package com.bs.odontograma.attachment.dto.response;

import com.bs.odontograma.attachment.enums.AttachmentCategory;
import com.bs.odontograma.attachment.enums.AttachmentOwnerType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AttachmentResponse {
    private UUID id;
    private UUID tenantId;
    private AttachmentOwnerType ownerType;
    private UUID ownerId;
    private Integer toothFdi;
    private UUID sessionId;
    private AttachmentCategory category;
    private String fileName;
    private String contentType;
    private Long sizeBytes;
    private String description;
    private LocalDate takenAt;
    private LocalDateTime createdAt;
    private String createdBy;

    /** Time-limited GET URL for the frontend to fetch the bytes directly from MinIO/S3. */
    private String downloadUrl;
}
