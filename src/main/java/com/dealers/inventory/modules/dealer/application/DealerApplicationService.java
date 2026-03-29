package com.dealers.inventory.modules.dealer.application;

import com.dealers.inventory.common.domain.SubscriptionType;
import com.dealers.inventory.common.exception.CrossTenantAccessException;
import com.dealers.inventory.common.exception.ResourceNotFoundException;
import com.dealers.inventory.modules.dealer.api.dto.DealerCreateRequest;
import com.dealers.inventory.modules.dealer.api.dto.DealerResponse;
import com.dealers.inventory.modules.dealer.api.dto.DealerUpdateRequest;
import com.dealers.inventory.modules.dealer.domain.Dealer;
import com.dealers.inventory.modules.dealer.repository.DealerRepository;
import com.dealers.inventory.modules.vehicle.repository.VehicleRepository;
import com.dealers.inventory.tenant.TenantContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DealerApplicationService {

    private final DealerRepository dealerRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public DealerResponse create(DealerCreateRequest request) {
        UUID tenantId = TenantContext.requireTenantId();
        Dealer dealer = Dealer.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .subscriptionType(request.getSubscriptionType())
                .build();
        return toResponse(dealerRepository.save(dealer));
    }

    @Transactional(readOnly = true)
    public DealerResponse get(UUID id) {
        return toResponse(loadOwned(id));
    }

    @Transactional(readOnly = true)
    public Page<DealerResponse> list(Pageable pageable) {
        return dealerRepository
                .findAllByTenantId(TenantContext.requireTenantId(), pageable)
                .map(this::toResponse);
    }

    @Transactional
    public DealerResponse update(UUID id, DealerUpdateRequest request) {
        Dealer dealer = loadOwned(id);
        if (request.getName() != null) {
            dealer.setName(request.getName().trim());
        }
        if (request.getEmail() != null) {
            dealer.setEmail(request.getEmail().trim().toLowerCase());
        }
        if (request.getSubscriptionType() != null) {
            dealer.setSubscriptionType(request.getSubscriptionType());
        }
        return toResponse(dealerRepository.save(dealer));
    }

    @Transactional
    public void delete(UUID id) {
        Dealer dealer = loadOwned(id);
        vehicleRepository.deleteAllByDealer_Id(dealer.getId());
        dealerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Dealer getOwnedDealerEntity(UUID id) {
        return loadOwned(id);
    }

    private Dealer loadOwned(UUID id) {
        Dealer dealer = dealerRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found"));
        UUID tenantId = TenantContext.requireTenantId();
        if (!dealer.getTenantId().equals(tenantId)) {
            throw new CrossTenantAccessException("Cross-tenant access is not allowed");
        }
        return dealer;
    }

    private DealerResponse toResponse(Dealer dealer) {
        return DealerResponse.builder()
                .id(dealer.getId())
                .tenantId(dealer.getTenantId())
                .name(dealer.getName())
                .email(dealer.getEmail())
                .subscriptionType(dealer.getSubscriptionType())
                .build();
    }
}
