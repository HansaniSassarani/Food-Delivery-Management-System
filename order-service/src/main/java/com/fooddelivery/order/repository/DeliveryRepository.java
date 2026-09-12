package com.fooddelivery.order.repository;

import com.fooddelivery.order.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByDeliveryStatus(String status);
    List<Delivery> findByDeliveryPersonId(Long deliveryPersonId);
}
