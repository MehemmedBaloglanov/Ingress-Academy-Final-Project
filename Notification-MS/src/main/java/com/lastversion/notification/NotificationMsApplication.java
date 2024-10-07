package com.lastversion.notification;

import com.lastversion.notification.consumer.KafkaConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class NotificationMsApplication implements CommandLineRunner {

    private final KafkaConsumerService kafkaConsumerService;
    public static void main(String[] args) {
        SpringApplication.run(NotificationMsApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
