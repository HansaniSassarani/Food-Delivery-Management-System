package com.fooddelivery.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptQuotationRequest {
    @NotNull
    private Long quotationId;
}
