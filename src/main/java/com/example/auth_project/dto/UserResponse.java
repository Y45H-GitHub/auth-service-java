package com.example.auth_project.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID userId;
    private String username;
    private String role;
}
