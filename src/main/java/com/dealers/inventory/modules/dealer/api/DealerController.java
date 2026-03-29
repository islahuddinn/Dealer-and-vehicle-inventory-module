package com.dealers.inventory.modules.dealer.api;

import com.dealers.inventory.modules.dealer.api.dto.DealerCreateRequest;
import com.dealers.inventory.modules.dealer.api.dto.DealerResponse;
import com.dealers.inventory.modules.dealer.api.dto.DealerUpdateRequest;
import com.dealers.inventory.modules.dealer.application.DealerApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dealers")
@RequiredArgsConstructor
@Tag(name = "Dealers")
@SecurityRequirement(name = "basicAuth")
@SecurityRequirement(name = "tenantHeader")
public class DealerController {

    private final DealerApplicationService dealerApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a dealer (tenant bound to X-Tenant-Id)")
    public DealerResponse create(@Valid @RequestBody DealerCreateRequest request) {
        return dealerApplicationService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dealer by id within current tenant")
    public DealerResponse get(@PathVariable UUID id) {
        return dealerApplicationService.get(id);
    }

    @GetMapping
    @Operation(summary = "List dealers for current tenant (pagination & sort)")
    public Page<DealerResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        return dealerApplicationService.list(pageable);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a dealer")
    public DealerResponse patch(@PathVariable UUID id, @Valid @RequestBody DealerUpdateRequest request) {
        return dealerApplicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a dealer")
    public void delete(@PathVariable UUID id) {
        dealerApplicationService.delete(id);
    }
}
