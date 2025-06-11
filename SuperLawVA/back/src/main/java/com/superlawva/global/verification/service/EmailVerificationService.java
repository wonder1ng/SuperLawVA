package com.superlawva.global.verification.service;

import com.superlawva.global.mail.MailSender;
import com.superlawva.global.verification.entity.EmailVerificationToken;
import com.superlawva.global.verification.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationService {

    private final EmailVerificationRepository repo;
    private final MailSender mail;

    private static final Duration TTL = Duration.ofMinutes(10);

    /* 인증 번호 발송 */
    public void send(String email) {
        if (repo.existsByEmailAndVerifiedIsTrue(email)) return;   // 이미 인증됨

        EmailVerificationToken token = EmailVerificationToken.issue(email, TTL);
        repo.save(token);

        String html = """
            <h3>[SuperLawVA] 이메일 인증번호</h3>
            <p>아래 숫자 6자리를 10분 안에 입력하세요.</p>
            <h1>%s</h1>
            """.formatted(token.getCode());

        mail.send(email, "[SuperLawVA] 이메일 인증", html);
    }

    /* 인증 번호 검증 */
    public void verify(String email, String code) {
        EmailVerificationToken token = repo.findByEmailAndCode(email, code)
                .orElseThrow(() -> new IllegalArgumentException("인증번호가 올바르지 않습니다."));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("인증번호가 만료되었습니다.");
        }
        token.markVerified();   // dirty checking
    }

    /* 만료 토큰 정리 (스케줄러 호출용) */
    public void cleanExpired() {
        repo.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
