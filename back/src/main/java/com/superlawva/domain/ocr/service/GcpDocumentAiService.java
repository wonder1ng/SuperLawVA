package com.superlawva.domain.ocr.service;

import java.util.Base64;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.protobuf.ByteString;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "gcp.enabled", havingValue = "true")
public class GcpDocumentAiService {

    private final DocumentProcessorServiceClient client;

    /**
     * Document AI를 사용하여 문서에서 텍스트를 추출합니다.
     *
     * @param fileContent 파일의 바이트 배열 또는 Base64 인코딩된 문자열
     * @param mimeType 파일의 MIME 타입 (예: "application/pdf", "image/jpeg")
     * @return 추출된 텍스트와 신뢰도
     */
    public DocumentAiResult processDocument(byte[] fileContent, String mimeType) {
        try {
            log.info("Starting Document AI processing for file type: {}", mimeType);

            // RawDocument 객체 생성 (올바른 방법)
            RawDocument rawDocument = RawDocument.newBuilder()
                    .setContent(ByteString.copyFrom(fileContent))
                    .setMimeType(mimeType)
                    .build();

            // ProcessRequest 생성 (setRawDocument 사용)
            ProcessRequest request = ProcessRequest.newBuilder()
                    .setName("projects/ocrt-461104/locations/us/processors/7a892344d230c57f")
                    .setRawDocument(rawDocument)  // ✅ 수정: setDocument → setRawDocument
                    .build();

            log.info("Sending request to Document AI...");

            // Document AI 호출
            ProcessResponse response = client.processDocument(request);

            // 결과 추출
            Document processedDocument = response.getDocument();
            String extractedText = processedDocument.getText();

            // 신뢰도 계산 (수정된 방법)
            double totalConfidence = 0.0;
            int pageCount = processedDocument.getPagesCount();

            if (pageCount > 0) {
                for (Document.Page page : processedDocument.getPagesList()) {
                    // ✅ 수정: hasConfidence() → getConfidence() 직접 사용
                    if (page.hasLayout()) {
                        Document.Page.Layout layout = page.getLayout();
                        totalConfidence += layout.getConfidence();
                    }
                }
                totalConfidence /= pageCount;
            } else {
                totalConfidence = 0.85; // 기본값
            }

            log.info("Document AI processing completed. Text length: {}, Confidence: {}",
                    extractedText.length(), totalConfidence);

            return DocumentAiResult.builder()
                    .extractedText(extractedText)
                    .confidence(totalConfidence)
                    .pageCount(pageCount)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Document AI processing failed", e);
            return DocumentAiResult.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * Base64 인코딩된 파일 데이터를 처리합니다.
     */
    public DocumentAiResult processDocumentFromBase64(String base64Content, String mimeType) {
        try {
            byte[] fileContent = Base64.getDecoder().decode(base64Content);
            return processDocument(fileContent, mimeType);
        } catch (Exception e) {
            log.error("Failed to decode Base64 content", e);
            return DocumentAiResult.builder()
                    .success(false)
                    .errorMessage("Invalid Base64 content: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 테스트용 메서드 - 실제 파일 업로드 테스트에서는 사용하지 않음
     * API 테스트 시에는 /api/upload/ocr 엔드포인트를 사용하세요
     */
    public DocumentAiResult processTestDocument() {
        log.info("⚠️ 테스트 메서드 호출됨 - 실제 파일 업로드를 위해서는 /api/upload/ocr 엔드포인트를 사용하세요");

        // Mock 데이터 반환 (실제 파일 업로드 테스트용)
        log.warn("Mock 데이터 반환 중 - 실제 Google Document AI 테스트를 위해서는 이미지/PDF 파일을 업로드하세요");
        return DocumentAiResult.builder()
                .extractedText("임대차계약서\n소재지: 서울특별시 강남구 역삼동 123-45 아파트 101동 501호\n전용면적: 84.5㎡\n계약일: 2024년 1월 15일\n임대차기간: 2024년 2월 1일부터 2026년 1월 31일까지\n보증금: 50000만원\n월세: 200만원")
                .confidence(0.88) // Mock 데이터임을 표시
                .pageCount(1)
                .success(true)
                .build();
    }

    /**
     * 실제 파일 업로드를 통한 Document AI 처리 메서드
     * (향후 파일 업로드 API 구현 시 사용)
     */
    public DocumentAiResult processUploadedFile(byte[] fileData, String mimeType) {
        log.info("Processing uploaded file with Document AI. File size: {} bytes, MIME: {}",
                fileData.length, mimeType);

        // 실제 Document AI 호출
        return processDocument(fileData, mimeType);
    }

    @lombok.Builder
    @lombok.Getter
    public static class DocumentAiResult {
        private final String extractedText;
        private final double confidence;
        private final int pageCount;
        private final boolean success;
        private final String errorMessage;
    }
} 