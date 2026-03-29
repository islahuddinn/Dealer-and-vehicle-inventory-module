package com.dealers.inventory.modules.vehicle.application;

import com.dealers.inventory.common.domain.SubscriptionType;
import com.dealers.inventory.common.domain.VehicleStatus;
import com.dealers.inventory.common.exception.CrossTenantAccessException;
import com.dealers.inventory.common.exception.ResourceNotFoundException;
import com.dealers.inventory.modules.dealer.application.DealerApplicationService;
import com.dealers.inventory.modules.dealer.domain.Dealer;
import com.dealers.inventory.modules.vehicle.api.dto.VehicleCreateRequest;
import com.dealers.inventory.modules.vehicle.api.dto.VehicleResponse;
import com.dealers.inventory.modules.vehicle.api.dto.VehicleUpdateRequest;
import com.dealers.inventory.modules.vehicle.domain.Vehicle;
import com.dealers.inventory.modules.vehicle.repository.VehicleRepository;
import com.dealers.inventory.modules.vehicle.repository.VehicleSpecifications;
import com.dealers.inventory.tenant.TenantContext;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleApplicationService {

    private final VehicleRepository vehicleRepository;
    private final DealerApplicationService dealerApplicationService;

    @Transactional
    public VehicleResponse create(VehicleCreateRequest request) {
        UUID tenantId = TenantContext.requireTenantId();
        Dealer dealer = dealerApplicationService.getOwnedDealerEntity(request.getDealerId());
        Vehicle vehicle = Vehicle.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .dealer(dealer)
                .model(request.getModel().trim())
                .price(request.getPrice())
                .status(request.getStatus())
                .build();
        return toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public VehicleResponse get(UUID id) {
        return toResponse(loadOwned(id));
    }

    @Transactional(readOnly = true)
    public Page<VehicleResponse> list(
            Pageable pageable,
            String model,
            VehicleStatus status,
            BigDecimal priceMin,
            BigDecimal priceMax,
            SubscriptionType subscription) {
        UUID tenantId = TenantContext.requireTenantId();
        Specification<Vehicle> spec = VehicleSpecifications.build(
                tenantId, model, status, priceMin, priceMax, subscription);
        return vehicleRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional
    public VehicleResponse update(UUID id, VehicleUpdateRequest request) {
        Vehicle vehicle = loadOwned(id);
        if (request.getDealerId() != null) {
            Dealer dealer = dealerApplicationService.getOwnedDealerEntity(request.getDealerId());
            vehicle.setDealer(dealer);
        }
        if (request.getModel() != null) {
            vehicle.setModel(request.getModel().trim());
        }
        if (request.getPrice() != null) {
            vehicle.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }
        return toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(UUID id) {
        loadOwned(id);
        vehicleRepository.deleteById(id);
    }

    private Vehicle loadOwned(UUID id) {
        Vehicle vehicle = vehicleRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        UUID tenantId = TenantContext.requireTenantId();
        if (!vehicle.getTenantId().equals(tenantId)) {
            throw new CrossTenantAccessException("Cross-tenant access is not allowed");
        }
        return vehicle;
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .tenantId(vehicle.getTenantId())
                .dealerId(vehicle.getDealer().getId())
                .model(vehicle.getModel())
                .price(vehicle.getPrice())
                .status(vehicle.getStatus())
                .build();
    }
}
