package com.example.auth_project.service;

import com.example.auth_project.dto.LoginResponse;
import com.example.auth_project.dto.RegisterRequest;
import com.example.auth_project.entity.User;

public interface UserService {

    User register(RegisterRequest request);

    LoginResponse login(String username, String password);
}
