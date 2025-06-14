package com.springboot.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "gcp.enabled", havingValue = "true", matchIfMissing = false)
public class GcpConfig {

    @Value("${gcp.project-id:default-project}")
    private String projectId;

    @Value("${gcp.location:us}")
    private String location;

    @Value("${gcp.processor-id:default-processor}")
    private String processorId;

    @Value("${gcp.credentials.path:}")
    private Resource credentialsPath;

    // 운영 환경용 Base64 인코딩된 credentials
    @Value("${gcp.credentials.base64:}")
    private String credentialsBase64;

    @Bean
    @ConditionalOnProperty(name = "gcp.enabled", havingValue = "true")
    public GoogleCredentials googleCredentials() throws IOException {
        log.info("GCP 인증 정보 로드 중...");

        // 필수 설정 검증
        if (!StringUtils.hasText(projectId) || !StringUtils.hasText(location) || !StringUtils.hasText(processorId)) {
            throw new IllegalStateException("GCP 설정이 불완전합니다. project-id, location, processor-id가 모두 필요합니다.");
        }

        // 1. Base64 인코딩된 credentials가 있으면 우선 사용 (운영 환경)
        if (StringUtils.hasText(credentialsBase64)) {
            log.info("Base64 인코딩된 credentials 사용");
            byte[] decodedKey = Base64.getDecoder().decode(credentialsBase64);
            return GoogleCredentials.fromStream(new ByteArrayInputStream(decodedKey));
        }
        // 2. 파일 경로가 있으면 파일에서 읽기 (로컬 개발)
        else if (credentialsPath != null && credentialsPath.exists()) {
            log.info("파일에서 credentials 로드: {}", credentialsPath.getFilename());
            return GoogleCredentials.fromStream(credentialsPath.getInputStream());
        }
        // 3. 기본 credentials 사용 (GCP 환경)
        else {
            log.info("기본 Application Default Credentials 사용");
            return GoogleCredentials.getApplicationDefault();
        }
    }

    @Bean
    @ConditionalOnProperty(name = "gcp.enabled", havingValue = "true")
    public DocumentProcessorServiceClient documentProcessorServiceClient(GoogleCredentials credentials) {
        try {
            log.info("Document AI 클라이언트 초기화 중...");

            DocumentProcessorServiceSettings settings = DocumentProcessorServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            return DocumentProcessorServiceClient.create(settings);
        } catch (Exception e) {
            log.error("Document AI 클라이언트 초기화 실패: {}", e.getMessage());
            throw new RuntimeException("Document AI 클라이언트 초기화 실패", e);
        }
    }

    @Bean
    @ConditionalOnProperty(name = "gcp.enabled", havingValue = "true")
    public String processorName() {
        if (!StringUtils.hasText(projectId) || !StringUtils.hasText(location) || !StringUtils.hasText(processorId)) {
            throw new IllegalStateException("GCP 설정이 불완전합니다.");
        }
        return String.format("projects/%s/locations/%s/processors/%s",
                projectId, location, processorId);
    }
}