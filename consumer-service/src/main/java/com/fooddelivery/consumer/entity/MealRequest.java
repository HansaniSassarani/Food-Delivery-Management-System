package com.fooddelivery.consumer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long consumerId;

    @Column(nullable = false)
    private LocalDate requestDate;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime deliveryDate;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false, length = 500)
    private String foodDescription;

    @Column(nullable = false)
    private String status;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
