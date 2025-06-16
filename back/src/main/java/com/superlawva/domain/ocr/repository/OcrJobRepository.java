package com.superlawva.domain.ocr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.superlawva.domain.ocr.entity.OcrJob;

public interface OcrJobRepository extends JpaRepository<OcrJob, Long> {
    Optional<OcrJob> findByDocumentId(Long documentId);
    Optional<OcrJob> findTopByDocumentIdOrderByCreatedAtDesc(Long documentId);
}