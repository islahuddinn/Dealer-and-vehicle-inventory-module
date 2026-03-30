package com.dealers.inventory.modules.vehicle.api;

import com.dealers.inventory.common.domain.SubscriptionType;
import com.dealers.inventory.common.domain.VehicleStatus;
import com.dealers.inventory.modules.vehicle.api.dto.VehicleCreateRequest;
import com.dealers.inventory.modules.vehicle.api.dto.VehicleResponse;
import com.dealers.inventory.modules.vehicle.api.dto.VehicleUpdateRequest;
import com.dealers.inventory.modules.vehicle.application.VehicleApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles")
@SecurityRequirement(name = "basicAuth")
@SecurityRequirement(name = "tenantHeader")
public class VehicleController {

    private final VehicleApplicationService vehicleApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a vehicle for a dealer in the current tenant")
    public VehicleResponse create(@Valid @RequestBody VehicleCreateRequest request) {
        return vehicleApplicationService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by id within current tenant")
    public VehicleResponse get(@PathVariable(name = "id") UUID id) {
        return vehicleApplicationService.get(id);
    }

    @GetMapping
    @Operation(
            summary = "List vehicles with optional filters",
            description =
                    "Optional query `subscription=PREMIUM` returns only vehicles whose **dealer** has PREMIUM "
                            + "subscription; results remain scoped to X-Tenant-Id.")
    public Page<VehicleResponse> list(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(name = "model", required = false) String model,
            @RequestParam(name = "status", required = false) VehicleStatus status,
            @RequestParam(name = "priceMin", required = false) BigDecimal priceMin,
            @RequestParam(name = "priceMax", required = false) BigDecimal priceMax,
            @Parameter(description = "Filter by dealer subscription, e.g. PREMIUM")
                    @RequestParam(name = "subscription", required = false)
                    SubscriptionType subscription) {
        return vehicleApplicationService.list(pageable, model, status, priceMin, priceMax, subscription);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a vehicle")
    public VehicleResponse patch(@PathVariable(name = "id") UUID id, @Valid @RequestBody VehicleUpdateRequest request) {
        return vehicleApplicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a vehicle")
    public void delete(@PathVariable(name = "id") UUID id) {
        vehicleApplicationService.delete(id);
    }
}
