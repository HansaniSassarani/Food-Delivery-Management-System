package com.fooddelivery.consumer.repository;

import com.fooddelivery.consumer.entity.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    Optional<Consumer> findByEmail(String email);
    boolean existsByEmail(String email);
}
