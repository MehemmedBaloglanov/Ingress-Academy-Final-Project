package com.lastversion.user.service;

import com.lastversion.common.entity.UserEntity;
import com.lastversion.common.status.UserStatus;
import com.lastversion.notification.consumer.KafkaConsumerService;
import com.lastversion.user.dto.request.RegistrationRequestDto;
import com.lastversion.user.dto.response.ConfirmationResponseDto;
import com.lastversion.user.dto.response.RegistrationResponseDto;
import com.lastversion.user.exception.InvalidException;
import com.lastversion.user.exception.UserNotFoundException;
import com.lastversion.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final KafkaTemplate<String, UserEntity> kafkaTemplate;
    private final KafkaConsumerService kafkaConsumerService;
    private final PasswordEncoder passwordEncoder;
    @Override
    public RegistrationResponseDto registration(RegistrationRequestDto registrationRequestDto) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(registrationRequestDto.getEmail());
        UserEntity user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getStatus() == UserStatus.EXPIRED) {
                user.setPin(generateRandomPin());
                user.setPinExpirationTime(LocalDateTime.now().plus(Duration.ofDays(2)));
                userRepository.save(user);
            } else if (user.getStatus() == UserStatus.NEW && !isPinValid(user.getPinExpirationTime())) {
                user.setPin(generateRandomPin());
                user.setPinExpirationTime(LocalDateTime.now().plus(Duration.ofDays(2)));
                userRepository.save(user);
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
                    .password(passwordEncoder.encode(registrationRequestDto.getPassword()))
                    .pin(generateRandomPin())
                    .createAt(now)
                    .pinExpirationTime(now.plus(Duration.ofMinutes(2)))
                    .status(UserStatus.NEW)
                    .build();
        }

        UserEntity savedUser = userRepository.save(user);
        kafkaTemplate.send(TOPIC_USER_REG_EVENTS, savedUser);

        kafkaConsumerService.consumeMessage(savedUser);

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
    public ConfirmationResponseDto confirmation(String email, String pin) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(email);
        }
        UserEntity user = userOptional.get();
        if (user.getPin().equals(pin) && isPinValid(user.getPinExpirationTime())) {
            user.setStatus(UserStatus.ACTIVATED);
            userRepository.save(user);
            return ConfirmationResponseDto.builder()
                    .message("Your account has been activated")
                    .build();
        } else {
            throw new InvalidException("Pin invalid");
        }
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
