package com.dealers.inventory.common.exception;

public class CrossTenantAccessException extends RuntimeException {

    public CrossTenantAccessException(String message) {
        super(message);
    }
}
