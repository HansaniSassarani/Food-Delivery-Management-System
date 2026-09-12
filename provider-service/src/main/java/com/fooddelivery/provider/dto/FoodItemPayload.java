package com.fooddelivery.provider.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FoodItemPayload {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Min(0)
    private Double price;
    @NotNull
    @Min(0)
    private Integer availableQuantity;
}
