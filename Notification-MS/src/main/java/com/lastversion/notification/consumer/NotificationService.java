package com.lastversion.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender javaMailSender;

    public void sendNotification(String email, String subject, String body) {
        log.info("Sending email to: {}", email);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("taxiapp.notifications@gmail.com"); // Sender address
            message.setTo(email);  // Recipient address
            message.setSubject(subject); // Subject
            message.setText(body); // Body

            // Send email
            javaMailSender.send(message);

            log.info("Email successfully sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send email to {}. Error: {}", email, e.getMessage());
        }
    }
}
