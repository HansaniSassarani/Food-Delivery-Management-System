package com.fooddelivery.consumer.controller;

import com.fooddelivery.consumer.dto.ConsumerResponse;
import com.fooddelivery.consumer.dto.ProfileUpdateRequest;
import com.fooddelivery.consumer.security.JwtService;
import com.fooddelivery.consumer.service.ConsumerService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consumers")
public class ConsumerController {
    private final ConsumerService consumerService;
    private final JwtService jwtService;

    public ConsumerController(ConsumerService consumerService, JwtService jwtService) {
        this.consumerService = consumerService;
        this.jwtService = jwtService;
    }

    @GetMapping("/profile")
    public ConsumerResponse profile(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return consumerService.getProfile(uid(auth));
    }

    @PutMapping("/profile")
    public ConsumerResponse update(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                                   @RequestBody ProfileUpdateRequest request) {
        return consumerService.updateProfile(uid(auth), request);
    }

    private Long uid(String auth) {
        Claims claims = jwtService.parse(auth.substring(7));
        return ((Number) claims.get("uid")).longValue();
    }
}
