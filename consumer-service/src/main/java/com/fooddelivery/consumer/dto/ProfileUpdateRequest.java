package com.fooddelivery.consumer.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String organizationName;
    private String contactNo;
    private String address;
    private Double latitude;
    private Double longitude;
}
