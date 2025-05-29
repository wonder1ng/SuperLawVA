package first.backtest.join.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    /**
     * 인증번호 생성 및 메일 전송 + Redis 저장 (5분 유효)
     */
    public void sendAuthMail(String toEmail, String authCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(toEmail);
            helper.setSubject("회원가입 인증 메일입니다.");
            helper.setText("<h3>인증번호: <b>" + authCode + "</b></h3>", true);
            helper.setFrom("your_email@gmail.com", "MyService");

            mailSender.send(message);
            System.out.println("Redis 저장 키: auth:" + toEmail + ", 인증번호: " + authCode);

            // Redis에 인증번호 저장 (5분 TTL)
            redisTemplate.opsForValue().set("auth:" + toEmail, authCode, Duration.ofMinutes(5));
        } catch (Exception e) {
            throw new RuntimeException("메일 전송 실패", e);
        }
    }

    /**
     * 이메일 인증번호 검증
     */
    public void verifyCode(String email, String inputCode) {
        String savedCode = redisTemplate.opsForValue().get("auth:" + email);
        System.out.println("Redis 조회 키: auth:" + email);
        System.out.println("입력값: " + inputCode + ", 저장된 값: " + savedCode);

        if (savedCode == null) {
            throw new RuntimeException("인증번호가 만료되었거나 존재하지 않습니다.");
        }

        if (!savedCode.equalsIgnoreCase(inputCode)) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }

        // 인증 성공 시 Redis에서 제거
        redisTemplate.delete("auth:" + email);
    }
}