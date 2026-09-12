package com.fooddelivery.order.repository;

import com.fooddelivery.order.entity.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findByConsumerIdOrderByCreatedAtDesc(Long consumerId);
    List<FoodOrder> findByProviderIdOrderByCreatedAtDesc(Long providerId);
    List<FoodOrder> findByDeliveryPersonIdOrderByCreatedAtDesc(Long deliveryPersonId);
}
