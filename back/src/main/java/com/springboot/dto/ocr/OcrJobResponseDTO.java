// OcrJobResponseDTO.java
package com.springboot.dto.ocr;

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
