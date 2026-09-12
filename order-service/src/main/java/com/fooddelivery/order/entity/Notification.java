package com.fooddelivery.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false, length = 500)
    private String message;
    @Column(nullable = false)
    private String notificationType;
    @Column(nullable = false)
    private Boolean isRead;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
