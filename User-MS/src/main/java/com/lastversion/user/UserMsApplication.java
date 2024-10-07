package com.lastversion.user;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@RequiredArgsConstructor
@EntityScan(basePackages = {"com.lastversion.common.entity"})
@ComponentScan(basePackages = {"com.lastversion.user", "com.lastversion.notification"})
public class UserMsApplication{
    public static void main(String[] args) {
        SpringApplication.run(UserMsApplication.class,args);
    }
}
