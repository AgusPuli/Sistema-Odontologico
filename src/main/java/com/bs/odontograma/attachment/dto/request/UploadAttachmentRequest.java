package com.bs.odontograma.attachment.dto.request;

import com.bs.odontograma.attachment.enums.AttachmentCategory;
import com.bs.odontograma.attachment.enums.AttachmentOwnerType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Metadata that accompanies the multipart file upload. Sent as form fields
 * alongside the {@code file} part.
 */
@Data
public class UploadAttachmentRequest {
    @NotNull private AttachmentOwnerType ownerType;
    @NotNull private UUID ownerId;
    @NotNull private AttachmentCategory category;

    /** Optional FDI for tooth-specific imaging. */
    private Integer toothFdi;

    /** Optional reference to the originating clinical session. */
    private UUID sessionId;

    private String description;
    private LocalDate takenAt;
}
