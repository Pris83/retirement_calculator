package com.example.retirementCalculator.exception;

public class CacheInitializationException extends RuntimeException {
    public CacheInitializationException(String message) {
        super(message);
    }

    public CacheInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
