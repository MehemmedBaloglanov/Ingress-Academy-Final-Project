package com.lastversion.user.rest;

import com.lastversion.user.dto.request.AdminRequestDto;
import com.lastversion.user.dto.request.RegistrationRequestDto;
import com.lastversion.user.dto.response.AdminResponseDto;
import com.lastversion.user.dto.response.ConfirmationResponseDto;
import com.lastversion.user.dto.response.RegistrationResponseDto;
import com.lastversion.user.security_and_jwt.dto.AuthenticationRequest;
import com.lastversion.user.security_and_jwt.dto.AuthenticationResponse;
import com.lastversion.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/1.0/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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
        AuthenticationResponse response = userService.authenticateUser(authenticationRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/authenticate")
    public ResponseEntity<AdminResponseDto> authenticateAdmin(@RequestBody AdminRequestDto adminRequestDto) throws Exception {
        AdminResponseDto response = userService.authenticateAdmin(adminRequestDto);
        return ResponseEntity.ok(response);
    }
}
