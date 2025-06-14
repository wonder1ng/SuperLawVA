package com.springboot.repository;

import com.springboot.entity.OcrJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OcrJobRepository extends JpaRepository<OcrJob, Long> {
    Optional<OcrJob> findByDocumentId(Long documentId);
    Optional<OcrJob> findTopByDocumentIdOrderByCreatedAtDesc(Long documentId);
}