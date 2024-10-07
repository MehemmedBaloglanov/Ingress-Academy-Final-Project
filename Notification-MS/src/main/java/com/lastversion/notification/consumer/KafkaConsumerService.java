package com.lastversion.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user-ms-registration-topic", groupId = "user-ms-group")
    public void consumeMessage(String message) {
        log.info("Received message: " + message);

        // Mesajı parse ediyoruz
        String[] parts = message.split(";"); // Mesajı ';' ile ayırıyoruz
        String subject = parts[0].split("=")[1];
        String body = parts[1].split("=")[1];
        String email = parts[2].split("=")[1];

        // E-posta gönderme işlemi
        notificationService.sendNotification(email, subject, body);
    }
}
