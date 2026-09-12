package com.fooddelivery.order.controller;

import com.fooddelivery.order.entity.Notification;
import com.fooddelivery.order.security.JwtService;
import com.fooddelivery.order.service.NotificationService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtService jwtService;

    public NotificationController(NotificationService notificationService, JwtService jwtService) {
        this.notificationService = notificationService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public List<Notification> list(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return notificationService.list(jwtService.uid(auth), jwtService.role(auth));
    }

    @PutMapping("/{id}/read")
    public Notification read(@PathVariable Long id) {
        return notificationService.markRead(id);
    }
}
