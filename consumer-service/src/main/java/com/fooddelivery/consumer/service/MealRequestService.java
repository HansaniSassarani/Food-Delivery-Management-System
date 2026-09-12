package com.fooddelivery.consumer.service;

import com.fooddelivery.consumer.dto.MealRequestPayload;
import com.fooddelivery.consumer.entity.MealRequest;
import com.fooddelivery.consumer.exception.ApiException;
import com.fooddelivery.consumer.repository.MealRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MealRequestService {
    public static final String PENDING = "PENDING";
    public static final String QUOTATION_RECEIVED = "QUOTATION_RECEIVED";
    public static final String QUOTATION_ACCEPTED = "QUOTATION_ACCEPTED";
    public static final String CANCELLED = "CANCELLED";

    private final MealRequestRepository mealRequestRepository;
    private final ConsumerService consumerService;

    public MealRequestService(MealRequestRepository mealRequestRepository, ConsumerService consumerService) {
        this.mealRequestRepository = mealRequestRepository;
        this.consumerService = consumerService;
    }

    public MealRequest create(Long consumerId, MealRequestPayload payload) {
        consumerService.find(consumerId);
        MealRequest request = MealRequest.builder()
                .consumerId(consumerId)
                .requestDate(LocalDate.now())
                .quantity(payload.getQuantity())
                .deliveryDate(payload.getDeliveryDate())
                .deliveryAddress(payload.getDeliveryAddress())
                .foodDescription(payload.getFoodDescription())
                .status(PENDING)
                .latitude(payload.getLatitude())
                .longitude(payload.getLongitude())
                .createdAt(LocalDateTime.now())
                .build();
        return mealRequestRepository.save(request);
    }

    public List<MealRequest> listMine(Long consumerId) {
        return mealRequestRepository.findByConsumerIdOrderByCreatedAtDesc(consumerId);
    }

    public List<MealRequest> listOpen() {
        return mealRequestRepository.findByStatusInOrderByCreatedAtDesc(List.of(PENDING, QUOTATION_RECEIVED));
    }

    public MealRequest get(Long id) {
        return mealRequestRepository.findById(id).orElseThrow(() -> new ApiException(404, "Meal request not found"));
    }

    public MealRequest update(Long consumerId, Long id, MealRequestPayload payload) {
        MealRequest request = getOwned(consumerId, id);
        if (QUOTATION_ACCEPTED.equals(request.getStatus())) {
            throw new ApiException(400, "Cannot update a request after a quotation is accepted");
        }
        request.setQuantity(payload.getQuantity());
        request.setDeliveryDate(payload.getDeliveryDate());
        request.setDeliveryAddress(payload.getDeliveryAddress());
        request.setFoodDescription(payload.getFoodDescription());
        if (payload.getLatitude() != null) request.setLatitude(payload.getLatitude());
        if (payload.getLongitude() != null) request.setLongitude(payload.getLongitude());
        return mealRequestRepository.save(request);
    }

    public void cancel(Long consumerId, Long id) {
        MealRequest request = getOwned(consumerId, id);
        if (QUOTATION_ACCEPTED.equals(request.getStatus())) {
            throw new ApiException(400, "Cannot cancel after a quotation is accepted");
        }
        request.setStatus(CANCELLED);
        mealRequestRepository.save(request);
    }

    public MealRequest updateStatus(Long id, String status) {
        MealRequest request = get(id);
        request.setStatus(status);
        return mealRequestRepository.save(request);
    }

    private MealRequest getOwned(Long consumerId, Long id) {
        MealRequest request = get(id);
        if (!request.getConsumerId().equals(consumerId)) {
            throw new ApiException(403, "You can only manage your own meal requests");
        }
        return request;
    }
}
