package com.fooddelivery.provider.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long mealRequestId;
    @Column(nullable = false)
    private Long providerId;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    private Integer estimatedTime;
    private String notes;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
