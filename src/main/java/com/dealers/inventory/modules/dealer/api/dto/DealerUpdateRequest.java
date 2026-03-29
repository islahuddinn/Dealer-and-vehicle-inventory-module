package com.dealers.inventory.modules.dealer.api.dto;

import com.dealers.inventory.common.domain.SubscriptionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DealerUpdateRequest {

    @Size(max = 255)
    private String name;

    @Email
    @Size(max = 320)
    private String email;

    private SubscriptionType subscriptionType;
}
