package com.fooddelivery.consumer.controller;

import com.fooddelivery.consumer.dto.AuthResponse;
import com.fooddelivery.consumer.dto.LoginRequest;
import com.fooddelivery.consumer.dto.RegisterRequest;
import com.fooddelivery.consumer.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/consumers/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/api/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
