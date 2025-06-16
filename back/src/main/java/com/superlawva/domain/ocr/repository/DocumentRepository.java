package com.superlawva.domain.ocr.repository;

import com.superlawva.domain.ocr.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Document> findByUserIdAndDocumentTypeOrderByCreatedAtDesc(Long userId, Document.DocumentType documentType);
}