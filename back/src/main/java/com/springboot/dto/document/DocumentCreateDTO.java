package com.springboot.dto.document;

import com.springboot.entity.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentCreateDTO {
    
    @NotBlank(message = "파일명은 필수입니다")
    private String originalFilename;
    
    private String mimeType;
    
    private Long fileSizeBytes;
    
    private Document.DocumentType documentType;
    
    // 테스트용: 기본값은 사용자 ID 1
    @Builder.Default
    private Long userId = 1L;
} 