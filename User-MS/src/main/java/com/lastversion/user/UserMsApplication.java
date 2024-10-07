package com.lastversion.user;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class UserMsApplication{
    public static void main(String[] args) {
        SpringApplication.run(UserMsApplication.class,args);
    }
}
