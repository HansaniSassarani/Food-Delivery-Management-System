package com.fooddelivery.order.repository;

import com.fooddelivery.order.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    Optional<DeliveryPerson> findByEmail(String email);
    boolean existsByEmail(String email);
    List<DeliveryPerson> findByAvailabilityStatus(String status);
}
