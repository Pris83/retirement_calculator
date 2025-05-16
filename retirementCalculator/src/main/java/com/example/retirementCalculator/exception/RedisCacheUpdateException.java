package com.example.retirementCalculator.exception;

public class RedisCacheUpdateException extends RuntimeException {
    public RedisCacheUpdateException(String message) {
        super(message);
    }

    public RedisCacheUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
