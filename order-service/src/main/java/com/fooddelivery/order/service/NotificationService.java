package com.fooddelivery.order.service;

import com.fooddelivery.order.entity.Notification;
import com.fooddelivery.order.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void notify(Long userId, String role, String type, String message) {
        notificationRepository.save(Notification.builder()
                .userId(userId)
                .role(role)
                .notificationType(type)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<Notification> list(Long userId, String role) {
        return notificationRepository.findByUserIdAndRoleOrderByCreatedAtDesc(userId, role);
    }

    public Notification markRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
}
