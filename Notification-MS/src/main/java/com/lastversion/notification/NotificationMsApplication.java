package com.lastversion.notification;

import com.lastversion.notification.consumer.KafkaConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class NotificationMsApplication {

    private final KafkaConsumerService kafkaConsumerService;
    public static void main(String[] args) {
        SpringApplication.run(NotificationMsApplication.class,args);
    }


}
