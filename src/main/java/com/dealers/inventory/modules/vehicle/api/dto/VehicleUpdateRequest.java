package com.dealers.inventory.modules.vehicle.api.dto;

import com.dealers.inventory.common.domain.VehicleStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class VehicleUpdateRequest {

    private UUID dealerId;

    @Size(max = 120)
    private String model;

    @Positive
    private BigDecimal price;

    private VehicleStatus status;
}
