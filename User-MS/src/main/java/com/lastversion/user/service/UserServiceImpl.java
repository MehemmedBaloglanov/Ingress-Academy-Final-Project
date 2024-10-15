package com.lastversion.user.service;

import com.lastversion.common.entity.AdminEntity;
import com.lastversion.common.entity.Role;
import com.lastversion.common.entity.UserEntity;
import com.lastversion.common.status.UserStatus;
import com.lastversion.notification.consumer.KafkaConsumerService;
import com.lastversion.user.dto.request.AdminRequestDto;
import com.lastversion.user.dto.request.RegistrationRequestDto;
import com.lastversion.user.dto.response.AdminResponseDto;
import com.lastversion.user.dto.response.ConfirmationResponseDto;
import com.lastversion.user.dto.response.RegistrationResponseDto;
import com.lastversion.user.exception.InvalidException;
import com.lastversion.user.exception.UserNotFoundException;
import com.lastversion.user.repository.AdminRepository;
import com.lastversion.user.repository.UserRepository;
import com.lastversion.user.security_and_jwt.dto.AuthenticationRequest;
import com.lastversion.user.security_and_jwt.dto.AuthenticationResponse;
import com.lastversion.user.security_and_jwt.service.MyUserDetailsService;
import com.lastversion.user.security_and_jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.lastversion.user.kafka.KafkaTopicConfiguration.TOPIC_USER_REG_EVENTS;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserEntity> kafkaTemplate;
    private final KafkaConsumerService kafkaConsumerService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MyUserDetailsService userDetailsService;
    private final RestTemplate restTemplate;
    private final AdminRepository  adminRepository;

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
                    .roles(Set.of(Role.USER))
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

        String courseMsUrl = "http://localhost:8080/courses/createstudent";
        restTemplate.postForObject(courseMsUrl, savedUser, String.class);

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

    @Override
    public AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final UserEntity userEntity = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new Exception("User not found"));

        Set<String> roles = userEntity.getRoles().stream()
                .map(role -> role.name())
                .collect(Collectors.toSet());

        final String jwt = jwtUtil.generateToken(userDetails.getUsername(), roles);

        return new AuthenticationResponse(jwt);
    }

    @Override
    public AdminResponseDto authenticateAdmin(AdminRequestDto adminRequestDto) throws Exception {
        Optional<AdminEntity> existingAdmin = adminRepository.findByEmail(adminRequestDto.getEmail());

        if (existingAdmin.isPresent()) {
            throw new Exception("Admin already exists with this email");
        }

        AdminEntity adminUser = AdminEntity.builder()
                .adminId(UUID.randomUUID())
                .firstName(adminRequestDto.getFirstName())
                .lastName(adminRequestDto.getLastName())
                .email(adminRequestDto.getEmail())
                .password(passwordEncoder.encode(adminRequestDto.getPassword()))
                .roles(Set.of(Role.ADMIN))
                .build();

        adminRepository.save(adminUser);

        final UserDetails userDetails = userDetailsService.loadUserByUsernameAndPassword(adminUser.getEmail(),adminUser.getPassword());

        Set<String> roles = adminUser.getRoles().stream()
                .map(role -> role.name())
                .collect(Collectors.toSet());

        final String jwt = jwtUtil.generateToken(userDetails.getUsername(), roles);

        return AdminResponseDto.builder()
                .firstName(adminUser.getFirstName())
                .lastName(adminUser.getLastName())
                .password(adminUser.getPassword())
                .email(adminUser.getEmail())
                .jwtToken(jwt)
                .build();
    }



    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (Exception e) {
            throw new Exception("Incorrect email or password", e);
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
