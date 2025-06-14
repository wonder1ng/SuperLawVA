package com.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.dto.document.DocumentResponseDTO;
import com.springboot.dto.ocr.OcrJobResponseDTO;
import com.springboot.dto.ocr.OcrRequestDTO;
import com.springboot.dto.ocr.OcrResultDTO;
import com.springboot.entity.Document;
import com.springboot.service.DocumentService;
import com.springboot.service.FileStorageService;
import com.springboot.service.GcpDocumentAiService;
import com.springboot.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Tag(name = "📁 파일 업로드 & OCR API", description = "파일 업로드 및 실시간 OCR 처리")
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final DocumentService documentService;
    private final Optional<GcpDocumentAiService> gcpDocumentAiService;
    private final OcrService ocrService;
    private final ObjectMapper objectMapper;

    @Operation(
        summary = "🚀 파일 업로드 + OCR 처리 + 데이터 파싱 (통합 API)",
        description = """
            계약서 이미지/PDF 파일을 업로드하고 Google Document AI로 OCR 처리 후 구조화된 JSON으로 변환하는 통합 API입니다.
            
            **지원 파일 형식:** PDF, JPG, JPEG, PNG, TIFF
            **최대 파일 크기:** 50MB
            
            **처리 과정:**
            1. 파일 업로드 및 암호화 저장
            2. Google Document AI OCR 처리 (텍스트 추출)
            3. 추출된 텍스트를 구조화된 JSON으로 파싱
            4. 계약서 정보 (부동산 표시, 계약 내용, 특약사항 등) 반환
            
            **반환 데이터:**
            - JSON 문자열 형태로 반환
            - 원본 OCR 텍스트
            - 구조화된 계약서 정보 (JSON)
            - 부동산 표시, 계약 내용, 집주인 정보, 부동산 사무실 정보
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OCR 처리 성공 (JSON 문자열 반환)",
                    content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 또는 요청"),
        @ApiResponse(responseCode = "500", description = "OCR 처리 실패")
    })
    @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> uploadAndProcessOcr(
            @Parameter(
                description = "업로드할 계약서 파일 (PDF, JPG, PNG 등)",
                required = true,
                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "OCR 처리 모드", example = "CONTRACT_PARSE")
            @RequestParam(value = "ocrMode", defaultValue = "CONTRACT_PARSE") String ocrMode,

            @Parameter(description = "품질 향상 옵션", example = "true")
            @RequestParam(value = "enhanceQuality", defaultValue = "true") boolean enhanceQuality,

            @Parameter(description = "사용자 ID", example = "1")
            @RequestParam(value = "userId", defaultValue = "1") Long userId) {

        try {
            log.info("=== 파일 업로드 + OCR 처리 시작 ===");
            log.info("파일명: {}, 크기: {} bytes, MIME: {}",
                    file.getOriginalFilename(), file.getSize(), file.getContentType());

            // 1. 파일 검증
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    objectMapper.writeValueAsString(Map.of("error", "파일이 비어있습니다."))
                );
            }

            // 파일 크기 검증 (50MB)
            if (file.getSize() > 50 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(
                    objectMapper.writeValueAsString(Map.of("error", "파일 크기가 너무 큽니다. (최대 50MB)"))
                );
            }

            // 파일 형식 검증
            String contentType = file.getContentType();
            if (contentType == null || !isValidFileType(contentType)) {
                return ResponseEntity.badRequest().body(
                    objectMapper.writeValueAsString(Map.of(
                        "error", "지원하지 않는 파일 형식입니다.",
                        "supportedTypes", "PDF, JPG, JPEG, PNG, TIFF"
                    ))
                );
            }

            // 2. 파일 저장
            String fileKey = fileStorageService.saveFile(file);
            log.info("파일 저장 완료: {}", fileKey);

            // 3. Document 엔티티 생성
            Document.DocumentType documentType = determineDocumentType(file.getOriginalFilename());

            DocumentResponseDTO document = documentService.createDocument(
                com.springboot.dto.document.DocumentCreateDTO.builder()
                    .originalFilename(file.getOriginalFilename())
                    .mimeType(file.getContentType())
                    .fileSizeBytes(file.getSize())
                    .documentType(documentType)
                    .userId(userId)
                    .build()
            );

            log.info("Document 생성 완료: ID = {}", document.getId());

            // 4. 🚀 실제 GCP Document AI 처리
            if (gcpDocumentAiService.isPresent()) {
                log.info("🔥 Google Document AI로 실제 OCR 처리 시작");

                GcpDocumentAiService.DocumentAiResult result = gcpDocumentAiService.get()
                    .processUploadedFile(file.getBytes(), file.getContentType());

                if (result.isSuccess()) {
                    log.info("✅ Document AI 성공! 추출된 텍스트 길이: {}, 신뢰도: {}",
                            result.getExtractedText().length(), result.getConfidence());

                    // 5. OCR 결과를 구조화된 JSON으로 변환
                    Map<String, Object> structuredResult = ocrService.convertToStructuredJson(
                        result.getExtractedText()
                    );

                    // 6. 메타데이터 추가
                    structuredResult.put("ocr_metadata", Map.of(
                        "document_id", document.getId(),
                        "original_filename", file.getOriginalFilename(),
                        "file_size", file.getSize(),
                        "confidence", result.getConfidence(),
                        "page_count", result.getPageCount(),
                        "processing_method", "Google Document AI",
                        "timestamp", java.time.LocalDateTime.now().toString(),
                        "success", true
                    ));

                    // JSON 문자열로 변환하여 반환 (Pretty Print)
                    ObjectMapper prettyMapper = new ObjectMapper();
                    prettyMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
                    return ResponseEntity.ok(prettyMapper.writeValueAsString(structuredResult));
                } else {
                    log.error("❌ Document AI 실패: {}", result.getErrorMessage());
                    return ResponseEntity.status(500).body(
                        objectMapper.writeValueAsString(Map.of(
                            "error", "OCR 처리 실패: " + result.getErrorMessage(),
                            "documentId", document.getId(),
                            "success", false
                        ))
                    );
                }
            } else {
                // 6. GCP 서비스가 비활성화된 경우 오류 반환
                log.error("❌ GCP Document AI 서비스가 비활성화되어 있습니다. OCR 처리를 할 수 없습니다.");
                return ResponseEntity.status(500).body(
                    objectMapper.writeValueAsString(Map.of(
                        "error", "GCP Document AI 서비스가 설정되지 않았습니다. 서비스 관리자에게 문의하세요.",
                        "documentId", document.getId(),
                        "success", false,
                        "service_status", "GCP_DOCUMENT_AI_DISABLED"
                    ))
                );
            }

        } catch (Exception e) {
            log.error("파일 업로드 + OCR 처리 실패", e);
            try {
                return ResponseEntity.status(500).body(
                    objectMapper.writeValueAsString(Map.of(
                        "error", "처리 중 오류 발생: " + e.getMessage(),
                        "success", false
                    ))
                );
            } catch (Exception jsonException) {
                return ResponseEntity.status(500).body(
                    "{\"error\":\"JSON 변환 오류\",\"success\":false}"
                );
            }
        }
    }

    @Operation(
        summary = "📄 간단한 파일 업로드",
        description = "파일만 업로드하고 나중에 OCR 처리하는 경우 사용"
    )
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "업로드할 파일", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "사용자 ID", example = "1")
            @RequestParam(value = "userId", defaultValue = "1") Long userId) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "파일이 비어있습니다."));
            }

            // 파일 저장
            String fileKey = fileStorageService.saveFile(file);

            // Document 생성
            DocumentResponseDTO document = documentService.createDocument(
                com.springboot.dto.document.DocumentCreateDTO.builder()
                    .originalFilename(file.getOriginalFilename())
                    .mimeType(file.getContentType())
                    .fileSizeBytes(file.getSize())
                    .documentType(determineDocumentType(file.getOriginalFilename()))
                    .userId(userId)
                    .build()
            );

            return ResponseEntity.ok(Map.of(
                "message", "파일 업로드 성공",
                "documentId", document.getId(),
                "filename", file.getOriginalFilename(),
                "fileKey", fileKey,
                "fileSize", file.getSize(),
                "success", true
            ));

        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "파일 업로드 실패: " + e.getMessage(),
                "success", false
            ));
        }
    }





    /**
     * 지원하는 파일 형식인지 확인
     */
    private boolean isValidFileType(String contentType) {
        return contentType.equals("application/pdf") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/tiff") ||
               contentType.equals("image/bmp");
    }

    /**
     * 파일명으로 문서 유형 판단
     */
    private Document.DocumentType determineDocumentType(String filename) {
        if (filename == null) return Document.DocumentType.OTHER;

        String lowerName = filename.toLowerCase();
        if (lowerName.contains("임대차") || lowerName.contains("계약서")) {
            if (lowerName.contains("월세")) {
                return Document.DocumentType.LEASE_MONTHLY;
            } else if (lowerName.contains("전세")) {
                return Document.DocumentType.LEASE_JEONSE;
            }
            return Document.DocumentType.LEASE_JEONSE; // 기본값
        } else if (lowerName.contains("증명서")) {
            return Document.DocumentType.CERTIFICATE;
        } else if (lowerName.contains("내용증명")) {
            return Document.DocumentType.CONTENT_PROOF;
        }

        return Document.DocumentType.OTHER;
    }
}
