package com.fooddelivery.provider.repository;

import com.fooddelivery.provider.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    List<Quotation> findByProviderIdOrderByCreatedAtDesc(Long providerId);
    List<Quotation> findByMealRequestIdOrderByCreatedAtDesc(Long mealRequestId);
    Optional<Quotation> findByMealRequestIdAndProviderId(Long mealRequestId, Long providerId);
}
