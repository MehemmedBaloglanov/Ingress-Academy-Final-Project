package com.lastversion.user.service;


import com.lastversion.user.dto.request.RegistrationRequestDto;
import com.lastversion.user.dto.response.ConfirmationResponseDto;
import com.lastversion.user.dto.response.RegistrationResponseDto;
import com.lastversion.user.entity.UserEntity;
import com.lastversion.user.exception.InvalidException;
import com.lastversion.user.exception.UserNotFoundException;
import com.lastversion.user.repository.UserRepository;
import com.lastversion.user.status.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.lastversion.user.kafka.KafkaTopicConfiguration.TOPIC_USER_REG_EVENTS;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final KafkaTemplate<String,UserEntity> kafkaTemplate;
//    private final KafkaProducerService kafkaProducerService;


    @Override
    public RegistrationResponseDto registration(RegistrationRequestDto registrationRequestDto) {

        Optional<UserEntity> userOptional = userRepository.findByEmail(registrationRequestDto.getEmail());
        UserEntity user = null;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getStatus() == UserStatus.EXPIRED) {
                user.setPin(generateRandomPin());
                user.setPinExpirationTime(LocalDateTime.now().plus(Duration.ofDays(2)));
                userRepository.save(user);
            } else if (user.getStatus() == UserStatus.NEW) {
                if (!isPinValid(user.getPinExpirationTime())) {
                    user.setPin(generateRandomPin());
                    user.setPinExpirationTime(LocalDateTime.now().plus(Duration.ofDays(2)));
                    userRepository.save(user);
                } else {
                    throw new RuntimeException("User already exists, check your email");
                }
            } else {
                throw new InvalidException("Email already in use");
            }
        } else {
            LocalDateTime now = LocalDateTime.now();
            user = UserEntity.builder()
                    .userId(UUID.randomUUID())
                    .firstName(registrationRequestDto.getFirstName())
                    .lastName(registrationRequestDto.getLastName())
                    .email(registrationRequestDto.getEmail())
                    .pin(generateRandomPin())
                    .createAt(now)
                    .pinExpirationTime(now.plus(Duration.ofMinutes(2)))
                    .status(UserStatus.NEW)
                    .build();
        }

        UserEntity savedUser = userRepository.save(user);

        kafkaTemplate.send(TOPIC_USER_REG_EVENTS,user);
        return RegistrationResponseDto.builder()
                .userId(savedUser.getUserId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .pin(savedUser.getPin())
                .createAt(savedUser.getCreateAt().toString())
                .pinExpirationTime(savedUser.getPinExpirationTime().toString())
                .status(savedUser.getStatus().toString())
                .build();
    }


    @Override
    public ConfirmationResponseDto confirmation(UUID userID) {
        Optional<UserEntity> userOptional = userRepository.findByUserId(userID);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        UserEntity userEntity = userOptional.get();

        if (!isPinValid(userEntity.getPinExpirationTime())) {
            userEntity.setStatus(UserStatus.EXPIRED);
            userRepository.save(userEntity);
            throw new InvalidException("User's pin is expired");
        }


        userEntity.setStatus(UserStatus.ACTIVATED);
        userEntity.setPin(userEntity.getPin());
        UserEntity savedUser = userRepository.save(userEntity);

        return ConfirmationResponseDto.builder()
                .confirmationPin(savedUser.getPin())
                .build();
    }

    private String generateRandomPin() {
        SecureRandom random = new SecureRandom();
        int pin = random.nextInt(900000) + 100000;
        return String.valueOf(pin);
    }

    private boolean isPinValid(LocalDateTime expireTime) {
        return LocalDateTime.now().isBefore(expireTime);
    }

}