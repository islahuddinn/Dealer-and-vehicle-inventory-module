package com.dealers.inventory.common.exception;

public class MissingTenantHeaderException extends RuntimeException {

    public MissingTenantHeaderException(String message) {
        super(message);
    }
}
