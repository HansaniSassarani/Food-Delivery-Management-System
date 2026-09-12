package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.AcceptQuotationRequest;
import com.fooddelivery.order.entity.Delivery;
import com.fooddelivery.order.entity.FoodOrder;
import com.fooddelivery.order.security.JwtService;
import com.fooddelivery.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final JwtService jwtService;

    public OrderController(OrderService orderService, JwtService jwtService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

    @PostMapping("/accept-quotation")
    public FoodOrder accept(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                            @Valid @RequestBody AcceptQuotationRequest request) {
        return orderService.acceptQuotation(jwtService.uid(auth), request.getQuotationId());
    }

    @GetMapping
    public List<FoodOrder> list(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return orderService.listFor(jwtService.role(auth), jwtService.uid(auth));
    }

    @GetMapping("/{id}")
    public FoodOrder get(@PathVariable Long id) {
        return orderService.get(id);
    }

    @PutMapping("/{id}/status")
    public FoodOrder status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orderService.updateStatus(id, body.get("status"));
    }

    @GetMapping("/{id}/track")
    public Map<String, Object> track(@PathVariable Long id) {
        FoodOrder order = orderService.get(id);
        Delivery delivery = orderService.trackByOrder(id);
        double distance = distanceKm(
                n(delivery.getCurrentLatitude()), n(delivery.getCurrentLongitude()),
                n(delivery.getDeliveryLatitude()), n(delivery.getDeliveryLongitude()));
        int eta = (int) Math.max(1, Math.round((distance / 25.0) * 60));
        return Map.of(
                "order", order,
                "delivery", delivery,
                "distanceKm", Math.round(distance * 100.0) / 100.0,
                "estimatedMinutes", eta
        );
    }

    private double n(Double value) {
        return value == null ? 0 : value;
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * r * Math.asin(Math.sqrt(a));
    }
}
