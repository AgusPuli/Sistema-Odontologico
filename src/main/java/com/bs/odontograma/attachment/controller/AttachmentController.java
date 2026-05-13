package com.bs.odontograma.attachment.controller;

import com.bs.odontograma.attachment.dto.request.UploadAttachmentRequest;
import com.bs.odontograma.attachment.dto.response.AttachmentResponse;
import com.bs.odontograma.attachment.enums.AttachmentOwnerType;
import com.bs.odontograma.attachment.service.AttachmentService;
import com.bs.odontograma.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Generic attachment endpoint. Same controller serves every owner type
 * (PATIENT / CLINICAL_SESSION / ESTIMATE / ...). Storage and presigning are
 * handled by {@link AttachmentService}, so frontends just consume metadata +
 * {@code downloadUrl}.
 *
 *   POST   /api/attachments         multipart upload (file + metadata fields)
 *   GET    /api/attachments?ownerType=PATIENT&ownerId={id}   list
 *   GET    /api/attachments/{id}    single
 *   DELETE /api/attachments/{id}    delete
 */
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachments", description = "Files (X-rays, photos, documents) attached to any owner entity")
public class AttachmentController {

    private final AttachmentService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Upload a new attachment (multipart)")
    public ResponseEntity<ApiResponse<AttachmentResponse>> upload(
            @RequestPart("file") MultipartFile file,
            @Valid @ModelAttribute UploadAttachmentRequest metadata
    ) throws IOException {
        AttachmentResponse response = service.upload(file, metadata);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "File uploaded"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "List attachments for an owner")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> list(
            @RequestParam AttachmentOwnerType ownerType,
            @RequestParam UUID ownerId
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.list(ownerType, ownerId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','MANAGER')")
    @Operation(summary = "Get a single attachment (refreshes the download URL)")
    public ResponseEntity<ApiResponse<AttachmentResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    @Operation(summary = "Delete an attachment (also removes the underlying file)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Attachment deleted"));
    }
}
