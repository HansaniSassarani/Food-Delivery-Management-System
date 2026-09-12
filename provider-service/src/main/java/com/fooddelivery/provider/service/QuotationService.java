package com.fooddelivery.provider.service;

import com.fooddelivery.provider.dto.QuotationPayload;
import com.fooddelivery.provider.entity.Provider;
import com.fooddelivery.provider.entity.Quotation;
import com.fooddelivery.provider.exception.ApiException;
import com.fooddelivery.provider.repository.QuotationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class QuotationService {
    private final QuotationRepository quotationRepository;
    private final ProviderService providerService;
    private final RestClient restClient;
    private final String consumerServiceUrl;

    public QuotationService(QuotationRepository quotationRepository,
                            ProviderService providerService,
                            @Value("${consumer.service.url}") String consumerServiceUrl) {
        this.quotationRepository = quotationRepository;
        this.providerService = providerService;
        this.consumerServiceUrl = consumerServiceUrl;
        this.restClient = RestClient.create();
    }

    public Object availableMealRequests(Long providerId) {
        Provider provider = providerService.get(providerId);
        if (!"AVAILABLE".equals(provider.getAvailabilityStatus())) {
            throw new ApiException(400, "Provider is not available to accept new requests");
        }
        return restClient.get()
                .uri(consumerServiceUrl + "/api/meal-requests/open")
                .retrieve()
                .body(Object.class);
    }

    public Quotation submit(Long providerId, QuotationPayload payload) {
        Provider provider = providerService.get(providerId);
        if (!"AVAILABLE".equals(provider.getAvailabilityStatus())) {
            throw new ApiException(400, "Provider is currently unavailable");
        }
        quotationRepository.findByMealRequestIdAndProviderId(payload.getMealRequestId(), providerId)
                .ifPresent(q -> {
                    throw new ApiException(409, "You already submitted a quotation for this request");
                });
        Quotation quotation = quotationRepository.save(Quotation.builder()
                .mealRequestId(payload.getMealRequestId())
                .providerId(providerId)
                .price(payload.getPrice())
                .estimatedTime(payload.getEstimatedTime())
                .notes(payload.getNotes())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build());
        try {
            restClient.put()
                    .uri(consumerServiceUrl + "/api/meal-requests/" + payload.getMealRequestId() + "/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("status", "QUOTATION_RECEIVED"))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
            // Consumer service may be temporarily unavailable; quotation is still stored.
        }
        return quotation;
    }

    public List<Quotation> byProvider(Long providerId) {
        return quotationRepository.findByProviderIdOrderByCreatedAtDesc(providerId);
    }

    public List<Quotation> byMealRequest(Long mealRequestId) {
        return quotationRepository.findByMealRequestIdOrderByCreatedAtDesc(mealRequestId);
    }

    public Quotation get(Long id) {
        return quotationRepository.findById(id).orElseThrow(() -> new ApiException(404, "Quotation not found"));
    }

    public Quotation updateStatus(Long id, String status) {
        Quotation quotation = get(id);
        quotation.setStatus(status);
        return quotationRepository.save(quotation);
    }
}
