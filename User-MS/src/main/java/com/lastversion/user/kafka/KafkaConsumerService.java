package com.lastversion.user.kafka;

import com.lastversion.user.email.EmailSenderService;
import com.lastversion.user.entity.UserEntity;
import com.lastversion.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.lastversion.user.kafka.KafkaTopicConfiguration.TOPIC_USER_REG_EVENTS;
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final EmailSenderService emailSenderService;

    @KafkaListener(topics = TOPIC_USER_REG_EVENTS, groupId = "user-ms")
    public void readMessage(UserEntity user) {
        log.info("Received User: {}", user);
        // E-posta içeriği oluştur ve gönder
        String emailBody = String.format("Merhaba %s %s,\n\nKayıt işleminiz başarıyla tamamlandı.\nPIN kodunuz: %s",
                user.getFirstName(), user.getLastName(), user.getPin());
        emailSenderService.sendEmail(user.getEmail(), "Kayıt Başarıyla Tamamlandı!", emailBody);
    }
}
