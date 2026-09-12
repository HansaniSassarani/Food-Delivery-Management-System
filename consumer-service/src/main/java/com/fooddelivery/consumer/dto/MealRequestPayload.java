package com.fooddelivery.consumer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MealRequestPayload {
    @NotNull
    @Min(1)
    private Integer quantity;
    @NotNull
    private LocalDateTime deliveryDate;
    @NotBlank
    private String deliveryAddress;
    @NotBlank
    private String foodDescription;
    private Double latitude;
    private Double longitude;
}
