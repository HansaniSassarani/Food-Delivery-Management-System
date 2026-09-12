package com.fooddelivery.consumer.service;

import com.fooddelivery.consumer.dto.ConsumerResponse;
import com.fooddelivery.consumer.dto.ProfileUpdateRequest;
import com.fooddelivery.consumer.entity.Consumer;
import com.fooddelivery.consumer.exception.ApiException;
import com.fooddelivery.consumer.repository.ConsumerRepository;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    private final ConsumerRepository consumerRepository;

    public ConsumerService(ConsumerRepository consumerRepository) {
        this.consumerRepository = consumerRepository;
    }

    public ConsumerResponse getProfile(Long id) {
        return toResponse(find(id));
    }

    public ConsumerResponse updateProfile(Long id, ProfileUpdateRequest request) {
        Consumer consumer = find(id);
        if (request.getOrganizationName() != null) consumer.setOrganizationName(request.getOrganizationName());
        if (request.getContactNo() != null) consumer.setContactNo(request.getContactNo());
        if (request.getAddress() != null) consumer.setAddress(request.getAddress());
        if (request.getLatitude() != null) consumer.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) consumer.setLongitude(request.getLongitude());
        return toResponse(consumerRepository.save(consumer));
    }

    public Consumer find(Long id) {
        return consumerRepository.findById(id).orElseThrow(() -> new ApiException(404, "Consumer not found"));
    }

    private ConsumerResponse toResponse(Consumer consumer) {
        return ConsumerResponse.builder()
                .id(consumer.getId())
                .organizationName(consumer.getOrganizationName())
                .email(consumer.getEmail())
                .contactNo(consumer.getContactNo())
                .address(consumer.getAddress())
                .latitude(consumer.getLatitude())
                .longitude(consumer.getLongitude())
                .createdAt(consumer.getCreatedAt())
                .build();
    }
}
