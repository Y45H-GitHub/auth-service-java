package com.example.auth_project.service.impl;

import com.example.auth_project.dto.LoginResponse;
import com.example.auth_project.dto.RegisterRequest;
import com.example.auth_project.entity.Role;
import com.example.auth_project.entity.User;
import com.example.auth_project.exception.PasswordMismatchException;
import com.example.auth_project.repo.UserRepo;
import com.example.auth_project.service.UserService;
import com.example.auth_project.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Override
    public User register(RegisterRequest request) {
        userRepo.findByUsername(request.getUsername())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("User with given username already exists");
                });
        User makeUser = new User();
        makeUser.setUsername(request.getUsername());
        makeUser.setPassword(passwordEncoder.encode(request.getPassword()));
        makeUser.setRole(Role.USER);
        return userRepo.save(makeUser);
    }

    @Override
    public LoginResponse login(String username, String password) {
        User current = userRepo.findByUsername(username)
                .orElseThrow(()->new EntityNotFoundException("User with given username not found"));

        if(!passwordEncoder.matches(password, current.getPassword())){
            throw new PasswordMismatchException("Password Incorrect");
        }

//        generate access token
        String accessToken = jwtUtil.generateAccessToken(current);
//        generate refresh token
        String refreshToken = jwtUtil.generateRefreshToken(current);
//        send the response
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUserId(current.getUserId());
        response.setUsername(current.getUsername());
        response.setRole(current.getRole().toString());

        return response;

    }


}
