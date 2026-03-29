package com.dealers.inventory.modules.vehicle.repository;

import com.dealers.inventory.common.domain.SubscriptionType;
import com.dealers.inventory.common.domain.VehicleStatus;
import com.dealers.inventory.modules.dealer.domain.Dealer;
import com.dealers.inventory.modules.vehicle.domain.Vehicle;
import jakarta.persistence.criteria.Join;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class VehicleSpecifications {

    private VehicleSpecifications() {}

    public static Specification<Vehicle> forTenant(UUID tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenantId"), tenantId);
    }

    public static Specification<Vehicle> modelEquals(String model) {
        if (model == null || model.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(cb.lower(root.get("model")), model.toLowerCase().trim());
    }

    public static Specification<Vehicle> statusEquals(VehicleStatus status) {
        if (status == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Vehicle> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> parts = new ArrayList<>();
            if (min != null) {
                parts.add(cb.greaterThanOrEqualTo(root.get("price"), min));
            }
            if (max != null) {
                parts.add(cb.lessThanOrEqualTo(root.get("price"), max));
            }
            if (parts.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(parts.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    /** When subscription=PREMIUM, restrict to vehicles whose dealer has PREMIUM subscription (same tenant). */
    public static Specification<Vehicle> dealerSubscriptionEquals(SubscriptionType subscriptionType) {
        if (subscriptionType == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            Join<Vehicle, Dealer> dealer = root.join("dealer");
            return cb.equal(dealer.get("subscriptionType"), subscriptionType);
        };
    }

    public static Specification<Vehicle> build(
            UUID tenantId,
            String model,
            VehicleStatus status,
            BigDecimal priceMin,
            BigDecimal priceMax,
            SubscriptionType subscriptionFilter) {
        Specification<Vehicle> spec = Specification.where(forTenant(tenantId));
        spec = spec.and(modelEquals(model));
        spec = spec.and(statusEquals(status));
        spec = spec.and(priceBetween(priceMin, priceMax));
        spec = spec.and(dealerSubscriptionEquals(subscriptionFilter));
        return spec;
    }
}
