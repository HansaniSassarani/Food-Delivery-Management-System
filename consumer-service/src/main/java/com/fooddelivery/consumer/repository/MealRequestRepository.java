package com.fooddelivery.consumer.repository;

import com.fooddelivery.consumer.entity.MealRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealRequestRepository extends JpaRepository<MealRequest, Long> {
    List<MealRequest> findByConsumerIdOrderByCreatedAtDesc(Long consumerId);
    List<MealRequest> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
}
