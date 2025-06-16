// OcrJobResponseDTO.java
package com.superlawva.domain.ocr.dto.ocr;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrJobResponseDTO {
    private String ocrJobId;
    private Long documentId;
    private String status;
    private String estimatedCompletionTime;
}
