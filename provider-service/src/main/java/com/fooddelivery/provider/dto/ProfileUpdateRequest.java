package com.fooddelivery.provider.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String organizationName;
    private String contactNo;
    private String address;
    private Integer capacity;
    private String availabilityStatus;
    private Double latitude;
    private Double longitude;
}
