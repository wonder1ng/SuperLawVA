// OcrStatusDTO.java
package com.springboot.dto.ocr;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrStatusDTO {
    private Long documentId;
    private String ocrStatus;
    private Double ocrConfidence;
    private String completedAt;
    private Integer processingTimeMs;
}
