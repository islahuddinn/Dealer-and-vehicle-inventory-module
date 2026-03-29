package com.dealers.inventory.tenant;

import java.util.Optional;
import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(UUID tenantId) {
        CURRENT.set(tenantId);
    }

    public static UUID requireTenantId() {
        return Optional.ofNullable(CURRENT.get())
                .orElseThrow(() -> new IllegalStateException("Tenant context is not set"));
    }

    public static Optional<UUID> currentTenantId() {
        return Optional.ofNullable(CURRENT.get());
    }

    public static void clear() {
        CURRENT.remove();
    }
}
