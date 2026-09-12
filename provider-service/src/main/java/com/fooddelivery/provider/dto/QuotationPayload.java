package com.fooddelivery.provider.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuotationPayload {
    @NotNull
    private Long mealRequestId;
    @NotNull
    @Min(0)
    private Double price;
    @NotNull
    @Min(1)
    private Integer estimatedTime;
    private String notes;
}
