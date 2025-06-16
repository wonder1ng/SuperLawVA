package com.superlawva.domain.user.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisEmailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 🟢 새로 추가: 이메일 발송 활성화 여부 설정
    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    /**
     * 인증번호 생성 및 메일 전송 + Redis 저장 (5분 유효)
     */
    public boolean sendAuthEmail(String toEmail, String authCode) {
        log.info("=== 이메일 인증 코드 발송 ===");
        log.info("대상 이메일: {}", toEmail);
        log.info("인증 코드: {}", authCode);
        log.info("이메일 발송 활성화: {}", emailEnabled);

        // 🟢 이메일 발송 활성화 여부 확인
        if (emailEnabled) {
            try {
                // 실제 이메일 발송
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

                helper.setTo(toEmail);
                helper.setSubject("AI 법률 서비스 - 이메일 인증");
                helper.setText(createVerificationEmailTemplate(authCode), true);
                helper.setFrom(fromEmail, "AI 법률 서비스");

                mailSender.send(message);
                log.info("✅ 실제 이메일 발송 성공: {}", toEmail);
            } catch (Exception e) {
                log.error("❌ 이메일 발송 실패: {}", e.getMessage());
                // 이메일 발송 실패해도 Redis 저장은 진행 (개발 모드)
                log.warn("🔧 개발 모드로 전환: Redis에만 인증 코드 저장");
            }
        } else {
            // 개발 모드: 실제 이메일 발송 없이 콘솔에만 출력
            log.warn("🔧 개발 모드: 실제 이메일 발송 안함");
            log.warn("📧 인증 코드: {}", authCode);
        }

        try {
            // Redis에 인증번호 저장 (5분 TTL) - 이메일 발송과 관계없이 항상 저장
            String redisKey = "auth:" + toEmail;
            redisTemplate.opsForValue().set(redisKey, authCode, Duration.ofMinutes(5));
            log.info("Redis 저장 완료 - 키: {}, 인증번호: {}", redisKey, authCode);
            return true;
        } catch (Exception e) {
            log.error("❌ Redis 저장 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 이메일 인증번호 검증
     */
    public void verifyCode(String email, String inputCode) {
        String redisKey = "auth:" + email;
        String savedCode = redisTemplate.opsForValue().get(redisKey);

        log.info("Redis 조회 - 키: {}, 입력값: {}, 저장된 값: {}", redisKey, inputCode, savedCode);

        if (savedCode == null) {
            throw new RuntimeException("인증번호가 만료되었거나 존재하지 않습니다.");
        }

        if (!savedCode.equalsIgnoreCase(inputCode)) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }

        // 인증 성공 시 Redis에서 제거
        redisTemplate.delete(redisKey);
        log.info("✅ 이메일 인증 성공: {}", email);
    }

    /**
     * 🟢 React 연동: 이메일 인증 템플릿
     */
    private String createVerificationEmailTemplate(String verificationCode) {
        return String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <h2 style="color: #007bff;">AI 법률 서비스</h2>
                <h3>이메일 인증</h3>
                <p>안녕하세요! 회원가입을 완료하기 위해 아래 인증 코드를 입력해주세요:</p>
                
                <div style="background: #f8f9fa; padding: 20px; text-align: center; font-size: 24px; font-weight: bold; color: #007bff; border-radius: 5px; margin: 20px 0;">
                    %s
                </div>
                
                <p><small>이 코드는 5분 동안 유효합니다.</small></p>
                <p><small>React 앱에서 이 코드를 입력하여 인증을 완료하세요.</small></p>
            </div>
            """, verificationCode);
    }

    /**
     * Redis에서 특정 키 삭제
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Redis 연결 상태 확인
     */
    public boolean isRedisConnected() {
        try {
            redisTemplate.opsForValue().set("legal-ai", "test", Duration.ofSeconds(1));
            return true;
        } catch (Exception e) {
            log.error("Redis 연결 실패: {}", e.getMessage());
            return false;
        }
    }
}