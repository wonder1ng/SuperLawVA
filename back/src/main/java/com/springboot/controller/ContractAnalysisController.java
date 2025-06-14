package com.springboot.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.service.ContractAnalysisService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
@Tag(name = "계약서 분석", description = "OCR 텍스트를 구조화된 JSON으로 변환")
public class ContractAnalysisController {

    private final ContractAnalysisService contractAnalysisService;

    @PostMapping("/analyze")
    @Operation(summary = "계약서 분석", description = "OCR로 추출된 계약서 텍스트를 분석하여 구조화된 정보를 반환합니다.")
    public ResponseEntity<Map<String, Object>> analyzeContract(@RequestBody Map<String, String> request) {
        log.info("계약서 분석 요청 받음");

        String ocrText = request.get("ocr_text");
        if (ocrText == null || ocrText.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "OCR 텍스트가 필요합니다."));
        }

        try {
            Map<String, Object> analysisResult = contractAnalysisService.analyzeContract(ocrText);
            return ResponseEntity.ok(analysisResult);
        } catch (Exception e) {
            log.error("계약서 분석 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "계약서 분석 중 오류가 발생했습니다."));
        }
    }
}