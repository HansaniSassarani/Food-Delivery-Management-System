package com.fooddelivery.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long mealRequestId;
    @Column(nullable = false)
    private Long quotationId;
    @Column(nullable = false)
    private Long consumerId;
    @Column(nullable = false)
    private Long providerId;
    private Long deliveryPersonId;
    @Column(nullable = false)
    private String orderStatus;
    @Column(nullable = false)
    private Double totalAmount;
    private Integer estimatedMinutes;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
