package com.fooddelivery.consumer.service;

import com.fooddelivery.consumer.dto.*;
import com.fooddelivery.consumer.entity.Consumer;
import com.fooddelivery.consumer.exception.ApiException;
import com.fooddelivery.consumer.repository.ConsumerRepository;
import com.fooddelivery.consumer.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final ConsumerRepository consumerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(ConsumerRepository consumerRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.consumerRepository = consumerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (consumerRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(409, "Email already registered");
        }
        Consumer consumer = Consumer.builder()
                .organizationName(request.getOrganizationName())
                .email(request.getEmail().toLowerCase())
                .contactNo(request.getContactNo())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .latitude(request.getLatitude() != null ? request.getLatitude() : 6.9271)
                .longitude(request.getLongitude() != null ? request.getLongitude() : 79.8612)
                .createdAt(LocalDateTime.now())
                .build();
        consumerRepository.save(consumer);
        String token = jwtService.generateToken(consumer.getId(), consumer.getEmail(), "CONSUMER");
        return new AuthResponse(token, "CONSUMER", consumer.getId(), consumer.getOrganizationName(), consumer.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        Consumer consumer = consumerRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new ApiException(401, "Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), consumer.getPassword())) {
            throw new ApiException(401, "Invalid credentials");
        }
        String token = jwtService.generateToken(consumer.getId(), consumer.getEmail(), "CONSUMER");
        return new AuthResponse(token, "CONSUMER", consumer.getId(), consumer.getOrganizationName(), consumer.getEmail());
    }
}
