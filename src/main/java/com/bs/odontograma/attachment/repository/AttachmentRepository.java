package com.bs.odontograma.attachment.repository;

import com.bs.odontograma.attachment.entity.Attachment;
import com.bs.odontograma.attachment.enums.AttachmentOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    Optional<Attachment> findByIdAndTenantId(UUID id, UUID tenantId);

    List<Attachment> findByTenantIdAndOwnerTypeAndOwnerIdOrderByCreatedAtDesc(
            UUID tenantId, AttachmentOwnerType ownerType, UUID ownerId
    );
}
