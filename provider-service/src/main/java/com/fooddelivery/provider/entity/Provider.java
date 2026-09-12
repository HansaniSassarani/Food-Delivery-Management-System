package com.fooddelivery.provider.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String organizationName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String contactNo;
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private Integer capacity;
    @Column(nullable = false)
    private String availabilityStatus;
    private Double latitude;
    private Double longitude;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
