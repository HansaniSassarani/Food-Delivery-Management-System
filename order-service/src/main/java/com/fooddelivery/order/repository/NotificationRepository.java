package com.fooddelivery.order.repository;

import com.fooddelivery.order.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndRoleOrderByCreatedAtDesc(Long userId, String role);
}
