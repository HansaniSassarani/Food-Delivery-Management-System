package com.fooddelivery.consumer.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConsumerResponse {
    private Long id;
    private String organizationName;
    private String email;
    private String contactNo;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
}
