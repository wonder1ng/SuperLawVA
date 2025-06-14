package com.springboot.dto.ocr;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
