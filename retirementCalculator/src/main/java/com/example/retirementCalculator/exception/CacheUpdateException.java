package com.example.retirementCalculator.exception;

public class CacheUpdateException extends RuntimeException {
    public CacheUpdateException(String message) {
        super(message);
    }

    public CacheUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
