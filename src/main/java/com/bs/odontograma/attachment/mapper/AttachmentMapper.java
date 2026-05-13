package com.bs.odontograma.attachment.mapper;

import com.bs.odontograma.attachment.dto.response.AttachmentResponse;
import com.bs.odontograma.attachment.entity.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    /** The downloadUrl is filled by the service after mapping (depends on TTL + presign). */
    @Mapping(target = "downloadUrl", ignore = true)
    AttachmentResponse toResponse(Attachment entity);
}
