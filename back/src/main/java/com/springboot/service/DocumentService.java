package com.springboot.service;

import com.springboot.dto.document.DocumentCreateDTO;
import com.springboot.dto.document.DocumentResponseDTO;
import com.springboot.entity.Document;
import com.springboot.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentResponseDTO createDocument(DocumentCreateDTO request) {
        log.info("Creating document: {}", request.getOriginalFilename());
        
        // Document 엔티티 생성
        Document document = Document.builder()
            .userId(request.getUserId())
            .originalFilename(request.getOriginalFilename())
            .encryptedFileKey(UUID.randomUUID().toString()) // 임시로 UUID 사용
            .documentType(request.getDocumentType() != null ? 
                request.getDocumentType() : Document.DocumentType.OTHER)
            .mimeType(request.getMimeType() != null ? 
                request.getMimeType() : "application/pdf")
            .fileSizeBytes(request.getFileSizeBytes() != null ? 
                request.getFileSizeBytes() : 1024000L)
            .status(Document.DocumentStatus.UPLOADED)
            .build();
        
        document = documentRepository.save(document);
        
        log.info("Document created successfully with ID: {}", document.getId());
        return DocumentResponseDTO.fromEntity(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getDocuments(Long userId) {
        List<Document> documents = documentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return documents.stream()
            .map(DocumentResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentResponseDTO getDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        return DocumentResponseDTO.fromEntity(document);
    }

    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        
        documentRepository.delete(document);
        log.info("Document deleted successfully: {}", documentId);
    }
} 