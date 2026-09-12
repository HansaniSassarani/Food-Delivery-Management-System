package com.fooddelivery.provider.controller;

import com.fooddelivery.provider.dto.FoodItemPayload;
import com.fooddelivery.provider.entity.FoodItem;
import com.fooddelivery.provider.security.JwtService;
import com.fooddelivery.provider.service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/food-items")
public class FoodItemController {
    private final FoodItemService foodItemService;
    private final JwtService jwtService;

    public FoodItemController(FoodItemService foodItemService, JwtService jwtService) {
        this.foodItemService = foodItemService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public FoodItem create(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                           @Valid @RequestBody FoodItemPayload payload) {
        return foodItemService.create(jwtService.uid(auth), payload);
    }

    @GetMapping
    public List<FoodItem> list(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return foodItemService.list(jwtService.uid(auth));
    }

    @PutMapping("/{id}")
    public FoodItem update(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                           @PathVariable Long id,
                           @Valid @RequestBody FoodItemPayload payload) {
        return foodItemService.update(jwtService.uid(auth), id, payload);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @PathVariable Long id) {
        foodItemService.delete(jwtService.uid(auth), id);
        return Map.of("message", "Food item deleted");
    }
}
