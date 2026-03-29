package com.dealers.inventory.modules.admin.api;

import com.dealers.inventory.modules.admin.application.AdminDealerQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dealers")
@RequiredArgsConstructor
@Tag(name = "Admin — Dealers")
@SecurityRequirement(name = "basicAuth")
public class AdminDealerController {

    private final AdminDealerQueryService adminDealerQueryService;

    @GetMapping("/countBySubscription")
    @Operation(
            summary = "Count dealers by subscription (GLOBAL_ADMIN)",
            description =
                    "Returns counts keyed by `BASIC` and `PREMIUM`. **Scope:** system-wide across **all tenants** "
                            + "(this endpoint does not use X-Tenant-Id). Requires GLOBAL_ADMIN.")
    public Map<String, Long> countBySubscription() {
        return adminDealerQueryService.countDealersBySubscription();
    }
}
