package com.lastversion.notification.consumer;

import com.lastversion.common.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user-ms-registration-topic", groupId = "user-ms-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeMessage(UserEntity userEntity) {
        log.info("Received UserEntity: {}", userEntity);

        String firstName = userEntity.getFirstName();
        String lastName = userEntity.getLastName();
        String email = userEntity.getEmail();
        String pin = userEntity.getPin();

        log.info("PIN: {}", pin);

        String subject = "Welcome to Our Service";
        String body = "Dear " + firstName + " " + lastName + ",\n\n" +
                "Thank you for registering. We are excited to have you on board!\n" +
                "Your registration PIN is: " + pin;

        notificationService.sendNotification(email, subject, body);
    }
}

