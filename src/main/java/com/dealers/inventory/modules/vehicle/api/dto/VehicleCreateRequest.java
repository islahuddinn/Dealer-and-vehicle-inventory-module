package com.dealers.inventory.modules.vehicle.api.dto;

import com.dealers.inventory.common.domain.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class VehicleCreateRequest {

    @NotNull private UUID dealerId;

    @NotBlank
    @Size(max = 120)
    private String model;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull private VehicleStatus status;
}
