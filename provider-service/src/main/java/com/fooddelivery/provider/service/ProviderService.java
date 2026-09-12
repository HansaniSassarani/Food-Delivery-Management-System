package com.fooddelivery.provider.service;

import com.fooddelivery.provider.dto.AuthResponse;
import com.fooddelivery.provider.dto.LoginRequest;
import com.fooddelivery.provider.dto.ProfileUpdateRequest;
import com.fooddelivery.provider.dto.RegisterRequest;
import com.fooddelivery.provider.entity.Provider;
import com.fooddelivery.provider.exception.ApiException;
import com.fooddelivery.provider.repository.ProviderRepository;
import com.fooddelivery.provider.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ProviderService(ProviderRepository providerRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.providerRepository = providerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (providerRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(409, "Email already registered");
        }
        Provider provider = Provider.builder()
                .organizationName(request.getOrganizationName())
                .email(request.getEmail().toLowerCase())
                .contactNo(request.getContactNo())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .capacity(request.getCapacity() != null ? request.getCapacity() : 10)
                .availabilityStatus("AVAILABLE")
                .latitude(request.getLatitude() != null ? request.getLatitude() : 6.9147)
                .longitude(request.getLongitude() != null ? request.getLongitude() : 79.8728)
                .createdAt(LocalDateTime.now())
                .build();
        providerRepository.save(provider);
        return token(provider);
    }

    public AuthResponse login(LoginRequest request) {
        Provider provider = providerRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new ApiException(401, "Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), provider.getPassword())) {
            throw new ApiException(401, "Invalid credentials");
        }
        return token(provider);
    }

    public Provider get(Long id) {
        return providerRepository.findById(id).orElseThrow(() -> new ApiException(404, "Provider not found"));
    }

    public Provider update(Long id, ProfileUpdateRequest request) {
        Provider provider = get(id);
        if (request.getOrganizationName() != null) provider.setOrganizationName(request.getOrganizationName());
        if (request.getContactNo() != null) provider.setContactNo(request.getContactNo());
        if (request.getAddress() != null) provider.setAddress(request.getAddress());
        if (request.getCapacity() != null) provider.setCapacity(request.getCapacity());
        if (request.getAvailabilityStatus() != null) provider.setAvailabilityStatus(request.getAvailabilityStatus());
        if (request.getLatitude() != null) provider.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) provider.setLongitude(request.getLongitude());
        return providerRepository.save(provider);
    }

    private AuthResponse token(Provider provider) {
        String jwt = jwtService.generateToken(provider.getId(), provider.getEmail(), "PROVIDER");
        return new AuthResponse(jwt, "PROVIDER", provider.getId(), provider.getOrganizationName(), provider.getEmail());
    }
}
