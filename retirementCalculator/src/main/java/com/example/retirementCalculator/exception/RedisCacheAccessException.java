package com.example.retirementCalculator.exception;

public class RedisCacheAccessException extends RuntimeException {
    public RedisCacheAccessException(String message) {
        super(message);
    }

    public RedisCacheAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
