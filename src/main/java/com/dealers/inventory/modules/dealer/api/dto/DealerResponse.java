package com.dealers.inventory.modules.dealer.api.dto;

import com.dealers.inventory.common.domain.SubscriptionType;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DealerResponse {
    UUID id;
    UUID tenantId;
    String name;
    String email;
    SubscriptionType subscriptionType;
}
