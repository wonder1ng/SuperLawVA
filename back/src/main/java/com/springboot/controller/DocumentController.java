package com.springboot.controller;

import com.springboot.dto.document.DocumentCreateDTO;
import com.springboot.dto.document.DocumentResponseDTO;
import com.springboot.entity.Document;
import com.springboot.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Document API", description = "문서 관리 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "문서 생성", 
              description = "새로운 문서를 생성합니다. (테스트용)")
    @PostMapping
    public ResponseEntity<DocumentResponseDTO> createDocument(
            @Valid @RequestBody DocumentCreateDTO request) {
        
        log.info("Creating document: {}", request.getOriginalFilename());
        
        DocumentResponseDTO response = documentService.createDocument(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "문서 목록 조회", 
              description = "사용자의 모든 문서를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<DocumentResponseDTO>> getDocuments(
            @RequestParam(required = false) Long userId) {
        
        List<DocumentResponseDTO> documents = documentService.getDocuments(userId != null ? userId : 1L);
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "문서 상세 조회", 
              description = "특정 문서의 상세 정보를 조회합니다.")
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> getDocument(@PathVariable Long documentId) {
        DocumentResponseDTO document = documentService.getDocument(documentId);
        return ResponseEntity.ok(document);
    }

    @Operation(summary = "문서 삭제", 
              description = "문서를 삭제합니다.")
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok().build();
    }
} 