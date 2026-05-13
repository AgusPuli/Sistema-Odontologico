package com.bs.odontograma.attachment.entity;

import com.bs.odontograma.attachment.enums.AttachmentCategory;
import com.bs.odontograma.attachment.enums.AttachmentOwnerType;
import com.bs.odontograma.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Generic file attachment. Owner is polymorphic (PATIENT, SESSION, ...). The
 * bytes live in object storage — this row only holds metadata + the storage key
 * we use to fetch or presign the actual file.
 */
@Entity
@Table(
        name = "attachments",
        indexes = {
                @Index(name = "idx_attachments_tenant", columnList = "tenant_id"),
                @Index(name = "idx_attachments_owner", columnList = "owner_type, owner_id"),
                @Index(name = "idx_attachments_tenant_owner", columnList = "tenant_id, owner_type, owner_id"),
                @Index(name = "idx_attachments_category", columnList = "category")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Attachment extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 40)
    private AttachmentOwnerType ownerType;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    /** Optional FDI for tooth-specific imaging (e.g. periapical X-ray of #16). */
    @Column(name = "tooth_fdi")
    private Integer toothFdi;

    /**
     * Optional reference to the clinical session this attachment was produced in.
     * Independent of owner_type so a PATIENT-owned X-ray can still link back to
     * the session where it was taken.
     */
    @Column(name = "session_id")
    private UUID sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AttachmentCategory category;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;

    @Column(name = "content_type", length = 150)
    private String contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** When the photo/X-ray was taken — independent of uploadedAt (createdAt). */
    @Column(name = "taken_at")
    private LocalDate takenAt;
}
