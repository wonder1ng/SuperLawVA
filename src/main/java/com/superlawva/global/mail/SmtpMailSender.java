package com.superlawva.global.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmtpMailSender implements MailSender {

    private final JavaMailSender sender;

    @Override
    public void send(String to, String subject, String html) {
        try {
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            sender.send(msg);
        } catch (Exception e) {
            throw new IllegalStateException("메일 전송 실패", e);
        }
    }
}
