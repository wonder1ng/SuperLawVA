package com.superlawva.domain.ocr.dto.document;

import com.superlawva.domain.ocr.entity.Document;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDTO {
    
    private Long id;
    private Long userId;
    private String originalFilename;
    private String encryptedFileKey;
    private Document.DocumentType documentType;
    private String mimeType;
    private Long fileSizeBytes;
    private Document.DocumentStatus status;
    private LocalDateTime createdAt;
    
    // 엔티티에서 DTO로 변환하는 정적 메서드
    public static DocumentResponseDTO fromEntity(Document document) {
        return DocumentResponseDTO.builder()
            .id(document.getId())
            .userId(document.getUserId())
            .originalFilename(document.getOriginalFilename())
            .encryptedFileKey(document.getEncryptedFileKey())
            .documentType(document.getDocumentType())
            .mimeType(document.getMimeType())
            .fileSizeBytes(document.getFileSizeBytes())
            .status(document.getStatus())
            .createdAt(document.getCreatedAt())
            .build();
    }
} 