package com.fooddelivery.order.service;

import com.fooddelivery.order.entity.Delivery;
import com.fooddelivery.order.entity.DeliveryPerson;
import com.fooddelivery.order.entity.FoodOrder;
import com.fooddelivery.order.exception.ApiException;
import com.fooddelivery.order.repository.DeliveryRepository;
import com.fooddelivery.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersonService deliveryPersonService;
    private final NotificationService notificationService;
    private final RestClient restClient;
    private final String consumerServiceUrl;
    private final String providerServiceUrl;

    public OrderService(OrderRepository orderRepository,
                        DeliveryRepository deliveryRepository,
                        DeliveryPersonService deliveryPersonService,
                        NotificationService notificationService,
                        @Value("${consumer.service.url}") String consumerServiceUrl,
                        @Value("${provider.service.url}") String providerServiceUrl) {
        this.orderRepository = orderRepository;
        this.deliveryRepository = deliveryRepository;
        this.deliveryPersonService = deliveryPersonService;
        this.notificationService = notificationService;
        this.consumerServiceUrl = consumerServiceUrl;
        this.providerServiceUrl = providerServiceUrl;
        this.restClient = RestClient.create();
    }

    @SuppressWarnings("unchecked")
    public FoodOrder acceptQuotation(Long consumerId, Long quotationId) {
        Map<String, Object> quotation = restClient.get()
                .uri(providerServiceUrl + "/api/quotations/" + quotationId)
                .retrieve()
                .body(Map.class);
        if (quotation == null) {
            throw new ApiException(404, "Quotation not found");
        }
        String qStatus = String.valueOf(quotation.get("status"));
        if (!"PENDING".equals(qStatus)) {
            throw new ApiException(400, "Quotation is not available");
        }
        Long mealRequestId = asLong(quotation.get("mealRequestId"));
        Long providerId = asLong(quotation.get("providerId"));
        Double price = asDouble(quotation.get("price"));
        Integer eta = quotation.get("estimatedTime") == null ? 30 : ((Number) quotation.get("estimatedTime")).intValue();

        Map<String, Object> meal = restClient.get()
                .uri(consumerServiceUrl + "/api/meal-requests/" + mealRequestId)
                .retrieve()
                .body(Map.class);
        if (meal == null) {
            throw new ApiException(404, "Meal request not found");
        }
        if (!consumerId.equals(asLong(meal.get("consumerId")))) {
            throw new ApiException(403, "You can only accept quotations for your own meal requests");
        }

        Map<String, Object> provider = restClient.get()
                .uri(providerServiceUrl + "/api/providers/" + providerId)
                .retrieve()
                .body(Map.class);

        FoodOrder order = orderRepository.save(FoodOrder.builder()
                .mealRequestId(mealRequestId)
                .quotationId(quotationId)
                .consumerId(consumerId)
                .providerId(providerId)
                .orderStatus("CREATED")
                .totalAmount(price)
                .estimatedMinutes(eta)
                .createdAt(LocalDateTime.now())
                .build());

        Delivery delivery = deliveryRepository.save(Delivery.builder()
                .orderId(order.getId())
                .pickupLatitude(asDouble(provider == null ? null : provider.get("latitude")))
                .pickupLongitude(asDouble(provider == null ? null : provider.get("longitude")))
                .deliveryLatitude(asDouble(meal.get("latitude")))
                .deliveryLongitude(asDouble(meal.get("longitude")))
                .currentLatitude(asDouble(provider == null ? null : provider.get("latitude")))
                .currentLongitude(asDouble(provider == null ? null : provider.get("longitude")))
                .deliveryStatus("ASSIGNED")
                .build());

        putJson(providerServiceUrl + "/api/quotations/" + quotationId + "/status", Map.of("status", "ACCEPTED"));
        putJson(consumerServiceUrl + "/api/meal-requests/" + mealRequestId + "/status", Map.of("status", "QUOTATION_ACCEPTED"));

        notificationService.notify(consumerId, "CONSUMER", "ORDER", "Order #" + order.getId() + " created. Waiting for a rider.");
        notificationService.notify(providerId, "PROVIDER", "ORDER", "Your quotation was accepted. Prepare order #" + order.getId());
        deliveryPersonService.available().forEach(person ->
                notificationService.notify(person.getId(), "DELIVERY", "DELIVERY_REQUEST",
                        "New delivery request for order #" + order.getId()));

        order.setOrderStatus("CONFIRMED");
        orderRepository.save(order);
        deliveryRepository.save(delivery);
        return order;
    }

    public List<FoodOrder> listFor(String role, Long userId) {
        return switch (role) {
            case "CONSUMER" -> orderRepository.findByConsumerIdOrderByCreatedAtDesc(userId);
            case "PROVIDER" -> orderRepository.findByProviderIdOrderByCreatedAtDesc(userId);
            case "DELIVERY" -> orderRepository.findByDeliveryPersonIdOrderByCreatedAtDesc(userId);
            default -> throw new ApiException(403, "Unsupported role");
        };
    }

    public FoodOrder get(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ApiException(404, "Order not found"));
    }

    public FoodOrder updateStatus(Long id, String status) {
        FoodOrder order = get(id);
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    public List<Delivery> openDeliveries() {
        return deliveryRepository.findByDeliveryStatus("ASSIGNED");
    }

    public Delivery acceptDelivery(Long deliveryId, Long personId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ApiException(404, "Delivery not found"));
        if (!"ASSIGNED".equals(delivery.getDeliveryStatus())) {
            throw new ApiException(400, "Delivery is no longer available");
        }
        DeliveryPerson person = deliveryPersonService.get(personId);
        if (!"AVAILABLE".equals(person.getAvailabilityStatus())) {
            throw new ApiException(400, "You are not available");
        }
        delivery.setDeliveryPersonId(personId);
        delivery.setDeliveryStatus("ACCEPTED");
        delivery.setCurrentLatitude(person.getCurrentLatitude());
        delivery.setCurrentLongitude(person.getCurrentLongitude());
        deliveryRepository.save(delivery);

        FoodOrder order = get(delivery.getOrderId());
        order.setDeliveryPersonId(personId);
        order.setOrderStatus("PREPARING");
        orderRepository.save(order);

        deliveryPersonService.setStatus(personId, "BUSY");
        notificationService.notify(order.getConsumerId(), "CONSUMER", "DELIVERY", "A rider accepted order #" + order.getId());
        notificationService.notify(order.getProviderId(), "PROVIDER", "DELIVERY", "Rider assigned to order #" + order.getId());
        return delivery;
    }

    public Delivery updateLocation(Long deliveryId, Long personId, Double lat, Double lng) {
        Delivery delivery = owned(deliveryId, personId);
        delivery.setCurrentLatitude(lat);
        delivery.setCurrentLongitude(lng);
        deliveryPersonService.updateLocation(personId, lat, lng);
        return deliveryRepository.save(delivery);
    }

    public Delivery updateDeliveryStatus(Long deliveryId, Long personId, String status) {
        Delivery delivery = owned(deliveryId, personId);
        delivery.setDeliveryStatus(status);
        FoodOrder order = get(delivery.getOrderId());
        switch (status) {
            case "PICKED_UP" -> order.setOrderStatus("READY_FOR_PICKUP");
            case "ON_THE_WAY" -> order.setOrderStatus("OUT_FOR_DELIVERY");
            case "DELIVERED" -> {
                order.setOrderStatus("DELIVERED");
                deliveryPersonService.setStatus(personId, "AVAILABLE");
                notificationService.notify(order.getConsumerId(), "CONSUMER", "DELIVERED", "Order #" + order.getId() + " delivered");
                notificationService.notify(order.getProviderId(), "PROVIDER", "DELIVERED", "Order #" + order.getId() + " delivered");
            }
            default -> {
            }
        }
        orderRepository.save(order);
        return deliveryRepository.save(delivery);
    }

    public Delivery trackByOrder(Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException(404, "Delivery not found"));
    }

    public List<Delivery> mine(Long personId) {
        return deliveryRepository.findByDeliveryPersonId(personId);
    }

    private Delivery owned(Long deliveryId, Long personId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ApiException(404, "Delivery not found"));
        if (!personId.equals(delivery.getDeliveryPersonId())) {
            throw new ApiException(403, "This delivery is not assigned to you");
        }
        return delivery;
    }

    private void putJson(String url, Map<String, String> body) {
        try {
            restClient.put().uri(url).contentType(MediaType.APPLICATION_JSON).body(body).retrieve().toBodilessEntity();
        } catch (Exception ignored) {
        }
    }

    private Long asLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private Double asDouble(Object value) {
        return value == null ? null : ((Number) value).doubleValue();
    }
}
