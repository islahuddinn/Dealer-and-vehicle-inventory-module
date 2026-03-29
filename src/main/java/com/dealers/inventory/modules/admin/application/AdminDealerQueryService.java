package com.dealers.inventory.modules.admin.application;

import com.dealers.inventory.common.domain.SubscriptionType;
import com.dealers.inventory.modules.dealer.repository.DealerRepository;
import com.dealers.inventory.modules.dealer.repository.DealerRepository.SubscriptionCountProjection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDealerQueryService {

    private final DealerRepository dealerRepository;

    /**
     * Returns dealer counts keyed by subscription type name.
     *
     * <p><b>Scope:</b> these counts are <b>system-wide (all tenants)</b>. The endpoint is restricted to
     * {@code GLOBAL_ADMIN} and is intentionally not scoped by {@code X-Tenant-Id}, so operations can see
     * aggregate inventory across the platform. If you need per-tenant rollups, add an optional tenant filter
     * behind the same admin role.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> countDealersBySubscription() {
        List<SubscriptionCountProjection> rows = dealerRepository.countGroupedBySubscription();
        Map<String, Long> result = new LinkedHashMap<>();
        result.put(SubscriptionType.BASIC.name(), 0L);
        result.put(SubscriptionType.PREMIUM.name(), 0L);
        for (SubscriptionCountProjection row : rows) {
            result.put(row.getSubscriptionType().name(), row.getCnt());
        }
        return result;
    }
}
