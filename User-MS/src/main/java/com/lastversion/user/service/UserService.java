package com.lastversion.user.service;


import com.lastversion.user.dto.request.RegistrationRequestDto;
import com.lastversion.user.dto.response.ConfirmationResponseDto;
import com.lastversion.user.dto.response.RegistrationResponseDto;
import jakarta.validation.Valid;

import java.util.UUID;

public interface UserService {
    RegistrationResponseDto registration(@Valid RegistrationRequestDto registrationRequestDto);

    ConfirmationResponseDto confirmation(UUID userID);
}
