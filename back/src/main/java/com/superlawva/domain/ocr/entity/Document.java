package com.superlawva.domain.ocr.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String originalFilename;
    
    @Column(nullable = false, unique = true)
    private String encryptedFileKey;
    
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    
    @Column(nullable = false)
    private String mimeType;
    
    private Long fileSizeBytes;
    
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum DocumentType {
        LEASE_JEONSE, LEASE_MONTHLY, CERTIFICATE, CONTENT_PROOF, OTHER
    }
    
    public enum DocumentStatus {
        UPLOADED, OCR_PROCESSING, OCR_COMPLETED, OCR_FAILED
    }
}