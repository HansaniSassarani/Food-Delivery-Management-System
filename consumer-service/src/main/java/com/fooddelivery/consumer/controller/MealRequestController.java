package com.fooddelivery.consumer.controller;

import com.fooddelivery.consumer.dto.MealRequestPayload;
import com.fooddelivery.consumer.entity.MealRequest;
import com.fooddelivery.consumer.security.JwtService;
import com.fooddelivery.consumer.service.MealRequestService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meal-requests")
public class MealRequestController {
    private final MealRequestService mealRequestService;
    private final JwtService jwtService;

    public MealRequestController(MealRequestService mealRequestService, JwtService jwtService) {
        this.mealRequestService = mealRequestService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public MealRequest create(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                              @Valid @RequestBody MealRequestPayload payload) {
        return mealRequestService.create(uid(auth), payload);
    }

    @GetMapping
    public List<MealRequest> mine(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return mealRequestService.listMine(uid(auth));
    }

    @GetMapping("/open")
    public List<MealRequest> open() {
        return mealRequestService.listOpen();
    }

    @GetMapping("/{id}")
    public MealRequest get(@PathVariable Long id) {
        return mealRequestService.get(id);
    }

    @PutMapping("/{id}")
    public MealRequest update(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                              @PathVariable Long id,
                              @Valid @RequestBody MealRequestPayload payload) {
        return mealRequestService.update(uid(auth), id, payload);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> cancel(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @PathVariable Long id) {
        mealRequestService.cancel(uid(auth), id);
        return Map.of("message", "Meal request cancelled");
    }

    @PutMapping("/{id}/status")
    public MealRequest status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return mealRequestService.updateStatus(id, body.get("status"));
    }

    private Long uid(String auth) {
        Claims claims = jwtService.parse(auth.substring(7));
        return ((Number) claims.get("uid")).longValue();
    }
}
