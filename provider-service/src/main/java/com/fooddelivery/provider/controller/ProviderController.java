package com.fooddelivery.provider.controller;

import com.fooddelivery.provider.dto.AuthResponse;
import com.fooddelivery.provider.dto.LoginRequest;
import com.fooddelivery.provider.dto.ProfileUpdateRequest;
import com.fooddelivery.provider.dto.RegisterRequest;
import com.fooddelivery.provider.entity.Provider;
import com.fooddelivery.provider.security.JwtService;
import com.fooddelivery.provider.service.ProviderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {
    private final ProviderService providerService;
    private final JwtService jwtService;

    public ProviderController(ProviderService providerService, JwtService jwtService) {
        this.providerService = providerService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return providerService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return providerService.login(request);
    }

    @GetMapping("/profile")
    public Provider profile(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return providerService.get(jwtService.uid(auth));
    }

    @PutMapping("/profile")
    public Provider update(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                           @RequestBody ProfileUpdateRequest request) {
        return providerService.update(jwtService.uid(auth), request);
    }

    @GetMapping("/{id}")
    public Provider byId(@PathVariable Long id) {
        return providerService.get(id);
    }
}
