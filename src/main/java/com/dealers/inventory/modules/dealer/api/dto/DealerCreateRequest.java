package com.dealers.inventory.modules.dealer.api.dto;

import com.dealers.inventory.common.domain.SubscriptionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DealerCreateRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Email
    @Size(max = 320)
    private String email;

    @NotNull private SubscriptionType subscriptionType;
}
