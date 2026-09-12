package com.fooddelivery.provider.service;

import com.fooddelivery.provider.dto.FoodItemPayload;
import com.fooddelivery.provider.entity.FoodItem;
import com.fooddelivery.provider.exception.ApiException;
import com.fooddelivery.provider.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodItemService {
    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public FoodItem create(Long providerId, FoodItemPayload payload) {
        return foodItemRepository.save(FoodItem.builder()
                .providerId(providerId)
                .name(payload.getName())
                .description(payload.getDescription())
                .price(payload.getPrice())
                .availableQuantity(payload.getAvailableQuantity())
                .build());
    }

    public List<FoodItem> list(Long providerId) {
        return foodItemRepository.findByProviderId(providerId);
    }

    public FoodItem update(Long providerId, Long id, FoodItemPayload payload) {
        FoodItem item = owned(providerId, id);
        item.setName(payload.getName());
        item.setDescription(payload.getDescription());
        item.setPrice(payload.getPrice());
        item.setAvailableQuantity(payload.getAvailableQuantity());
        return foodItemRepository.save(item);
    }

    public void delete(Long providerId, Long id) {
        foodItemRepository.delete(owned(providerId, id));
    }

    private FoodItem owned(Long providerId, Long id) {
        FoodItem item = foodItemRepository.findById(id).orElseThrow(() -> new ApiException(404, "Food item not found"));
        if (!item.getProviderId().equals(providerId)) {
            throw new ApiException(403, "You can only manage your own food items");
        }
        return item;
    }
}
