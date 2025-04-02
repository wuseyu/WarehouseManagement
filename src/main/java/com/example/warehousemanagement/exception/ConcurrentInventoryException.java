package com.example.warehousemanagement.exception;

public class ConcurrentInventoryException extends RuntimeException {
    public ConcurrentInventoryException(String message) {
        super(message);
    }
}