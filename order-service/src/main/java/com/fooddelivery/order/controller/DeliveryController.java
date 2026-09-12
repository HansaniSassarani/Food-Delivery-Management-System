package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.AuthResponse;
import com.fooddelivery.order.dto.DeliveryRegisterRequest;
import com.fooddelivery.order.dto.LoginRequest;
import com.fooddelivery.order.entity.Delivery;
import com.fooddelivery.order.entity.DeliveryPerson;
import com.fooddelivery.order.security.JwtService;
import com.fooddelivery.order.service.DeliveryPersonService;
import com.fooddelivery.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {
    private final DeliveryPersonService deliveryPersonService;
    private final OrderService orderService;
    private final JwtService jwtService;

    public DeliveryController(DeliveryPersonService deliveryPersonService, OrderService orderService, JwtService jwtService) {
        this.deliveryPersonService = deliveryPersonService;
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody DeliveryRegisterRequest request) {
        return deliveryPersonService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return deliveryPersonService.login(request);
    }

    @GetMapping("/profile")
    public DeliveryPerson profile(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return deliveryPersonService.get(jwtService.uid(auth));
    }

    @PutMapping("/status")
    public DeliveryPerson availability(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                                       @RequestBody Map<String, String> body) {
        return deliveryPersonService.setStatus(jwtService.uid(auth), body.get("status"));
    }

    @GetMapping("/available")
    public List<DeliveryPerson> available() {
        return deliveryPersonService.available();
    }

    @GetMapping("/requests")
    public List<Delivery> requests() {
        return orderService.openDeliveries();
    }

    @PostMapping("/assign")
    public List<Delivery> assign() {
        return orderService.openDeliveries();
    }

    @PostMapping("/{id}/accept")
    public Delivery accept(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @PathVariable Long id) {
        return orderService.acceptDelivery(id, jwtService.uid(auth));
    }

    @PutMapping("/{id}/location")
    public Delivery location(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                             @PathVariable Long id,
                             @RequestBody Map<String, Double> body) {
        return orderService.updateLocation(id, jwtService.uid(auth), body.get("latitude"), body.get("longitude"));
    }

    @PutMapping("/{id}/status")
    public Delivery status(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                           @PathVariable Long id,
                           @RequestBody Map<String, String> body) {
        return orderService.updateDeliveryStatus(id, jwtService.uid(auth), body.get("status"));
    }

    @GetMapping("/mine")
    public List<Delivery> mine(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return orderService.mine(jwtService.uid(auth));
    }
}
