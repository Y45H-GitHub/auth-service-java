package com.example.auth_project.controller;

import com.example.auth_project.dto.ApiResponse;
import com.example.auth_project.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        log.info("Authenticated user: {}", user.getUsername());
        
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", user));
    }

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> hello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        String message = "Hello, " + user.getUsername() + "! Your role is: " + user.getRole();
        
        return ResponseEntity.ok(ApiResponse.success("Hello message", message));
    }
}