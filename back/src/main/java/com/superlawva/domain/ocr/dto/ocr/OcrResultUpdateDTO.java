// OcrResultUpdateDTO.java
package com.superlawva.domain.ocr.dto.ocr;

import lombok.*;

import java.util.Map;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrResultUpdateDTO {
    private OcrResultDTO.ParsedContract parsedContract;
    private Map<String, Object> customFields;
}