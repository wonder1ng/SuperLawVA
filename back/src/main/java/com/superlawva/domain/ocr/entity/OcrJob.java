package com.superlawva.domain.ocr.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ocr_jobs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrJob {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long documentId;
    
    @Enumerated(EnumType.STRING)
    private OcrStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String rawText;
    
    private Double confidence;
    
    private Integer processingTimeMs;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    public enum OcrStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }
}

