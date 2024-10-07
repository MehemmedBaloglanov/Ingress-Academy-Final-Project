package com.lastversion.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RegistrationResponseDto {

    private UUID userId;

    private String firstName;

    private String lastName;

    private String email;

    private String pin;

    private String createAt;

    private String pinExpirationTime;

    private String status;
}
