package com.lastversion.user.service;


import com.lastversion.user.dto.request.AdminRequestDto;
import com.lastversion.user.dto.request.RegistrationRequestDto;
import com.lastversion.user.dto.response.AdminResponseDto;
import com.lastversion.user.dto.response.ConfirmationResponseDto;
import com.lastversion.user.dto.response.RegistrationResponseDto;
import com.lastversion.user.security_and_jwt.dto.AuthenticationRequest;
import com.lastversion.user.security_and_jwt.dto.AuthenticationResponse;

public interface UserService {
    RegistrationResponseDto registration(RegistrationRequestDto registrationRequestDto);

    ConfirmationResponseDto confirmation(String email, String pin);

    AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest) throws Exception;

    AdminResponseDto authenticateAdmin(AdminRequestDto adminRequestDto) throws Exception;
}
