package com.lastversion.user.rest;

import com.lastversion.user.dto.request.RegistrationRequestDto;
import com.lastversion.user.dto.response.ConfirmationResponseDto;
import com.lastversion.user.dto.response.RegistrationResponseDto;
import com.lastversion.user.security_and_jwt.dto.AuthenticationRequest;
import com.lastversion.user.security_and_jwt.dto.AuthenticationResponse;
import com.lastversion.user.security_and_jwt.service.MyUserDetailsService;
import com.lastversion.user.security_and_jwt.util.JwtUtil;
import com.lastversion.user.service.UserService;
import com.lastversion.common.entity.UserEntity;
import com.lastversion.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/1.0/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MyUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponseDto> registration(@Valid @RequestBody RegistrationRequestDto registrationRequestDto) {
        RegistrationResponseDto response = userService.registration(registrationRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirmation")
    public ResponseEntity<ConfirmationResponseDto> confirmation(@RequestParam String email, @RequestParam String pin) {
        ConfirmationResponseDto responseDto = userService.confirmation(email, pin);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        // İstifadəçi email və paroluna görə doğrulanır
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Incorrect email or password", e);
        }

        // İstifadəçi məlumatlarını yükləyirik
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        // İstifadəçinin rolunu alırıq
        final UserEntity userEntity = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new Exception("User not found"));

        Set<String> roles = userEntity.getRoles().stream()
                .map(role -> role.name()) // Rol enumunu string formata çeviririk
                .collect(Collectors.toSet());

        // JWT token yaradırıq, rolu da tokenə əlavə edirik
        final String jwt = jwtUtil.generateToken(userDetails.getUsername(), roles);

        // Tokeni cavab olaraq qaytarırıq
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
