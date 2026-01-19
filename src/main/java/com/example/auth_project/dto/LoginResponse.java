package com.example.auth_project.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponse {

    private String accessToken;
    private String refreshToken;  // Will be removed from response body after creating cookie
    private UUID userId;
    private String username;
    private String role;
}
