package com.fooddelivery.order.service;

import com.fooddelivery.order.dto.AuthResponse;
import com.fooddelivery.order.dto.DeliveryRegisterRequest;
import com.fooddelivery.order.dto.LoginRequest;
import com.fooddelivery.order.entity.DeliveryPerson;
import com.fooddelivery.order.exception.ApiException;
import com.fooddelivery.order.repository.DeliveryPersonRepository;
import com.fooddelivery.order.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryPersonService {
    private final DeliveryPersonRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public DeliveryPersonService(DeliveryPersonRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(DeliveryRegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new ApiException(409, "Email already registered");
        }
        DeliveryPerson person = DeliveryPerson.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .contactNo(request.getContactNo())
                .password(passwordEncoder.encode(request.getPassword()))
                .availabilityStatus("AVAILABLE")
                .currentLatitude(6.9271)
                .currentLongitude(79.8612)
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(person);
        return token(person);
    }

    public AuthResponse login(LoginRequest request) {
        DeliveryPerson person = repository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new ApiException(401, "Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), person.getPassword())) {
            throw new ApiException(401, "Invalid credentials");
        }
        return token(person);
    }

    public DeliveryPerson get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ApiException(404, "Delivery person not found"));
    }

    public DeliveryPerson setStatus(Long id, String status) {
        DeliveryPerson person = get(id);
        person.setAvailabilityStatus(status);
        return repository.save(person);
    }

    public List<DeliveryPerson> available() {
        return repository.findByAvailabilityStatus("AVAILABLE");
    }

    public DeliveryPerson updateLocation(Long id, Double lat, Double lng) {
        DeliveryPerson person = get(id);
        person.setCurrentLatitude(lat);
        person.setCurrentLongitude(lng);
        return repository.save(person);
    }

    private AuthResponse token(DeliveryPerson person) {
        String jwt = jwtService.generateToken(person.getId(), person.getEmail(), "DELIVERY");
        return new AuthResponse(jwt, "DELIVERY", person.getId(), person.getFullName(), person.getEmail());
    }
}
