package com.dealers.inventory.modules.vehicle.api.dto;

import com.dealers.inventory.common.domain.VehicleStatus;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VehicleResponse {
    UUID id;
    UUID tenantId;
    UUID dealerId;
    String model;
    BigDecimal price;
    VehicleStatus status;
}
