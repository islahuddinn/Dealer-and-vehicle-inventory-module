package com.dealers.inventory.modules.dealer.repository;

import com.dealers.inventory.common.domain.SubscriptionType;
import com.dealers.inventory.modules.dealer.domain.Dealer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DealerRepository extends JpaRepository<Dealer, UUID> {

    Optional<Dealer> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<Dealer> findAllByTenantId(UUID tenantId, Pageable pageable);

    boolean existsByEmail(String email);

    @Query("select d.subscriptionType as subscriptionType, count(d) as cnt from Dealer d group by d.subscriptionType")
    java.util.List<SubscriptionCountProjection> countGroupedBySubscription();

    interface SubscriptionCountProjection {
        SubscriptionType getSubscriptionType();

        long getCnt();
    }
}
