package com.example.retirementCalculator.exception;

public class RedisCacheDeleteException extends RuntimeException {
    public RedisCacheDeleteException(String message) {
        super(message);
    }

    public RedisCacheDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
