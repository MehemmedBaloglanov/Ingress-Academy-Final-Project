package com.lastversion.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender javaMailSender;

    public void sendNotification(String email, String subject, String body) {
        log.info("Sending email to: {}", email);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("taxiapp.notifications@gmail.com"); // Gönderici adresi
            message.setTo(email);  // Alıcı adresi
            message.setSubject(subject); // Konu
            message.setText(body); // Gövde

            // E-posta gönderimi
            javaMailSender.send(message);

            log.info("Email successfully sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send email to {}. Error: {}", email, e.getMessage());
        }
    }
}
