package com.superlawva.domain.ocr.dto.ocr;

import jakarta.validation.constraints.NotNull;
import lombok.*;

// OCR 요청 DTO
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrRequestDTO {
    @NotNull
    private String ocrMode;  // CONTRACT_PARSE, TEXT_ONLY
    
    @Builder.Default
    private Boolean enhanceQuality = true;
}
