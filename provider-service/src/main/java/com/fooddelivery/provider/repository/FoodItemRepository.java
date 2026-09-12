package com.fooddelivery.provider.repository;

import com.fooddelivery.provider.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByProviderId(Long providerId);
}
