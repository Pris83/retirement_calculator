package com.example.retirementCalculator.exception;

/**
 * Exception thrown when there is an error accessing the Redis cache.
 * <p>
 * This exception typically indicates a failure during read operations
 * from Redis, such as connection issues or data retrieval problems.
 * </p>
 *
 * Example usage:
 * <pre>
 *   throw new RedisCacheAccessException("Failed to fetch data from Redis cache");
 * </pre>
 *
 * This exception extends {@link RuntimeException} and can optionally
 * wrap an underlying cause.
 *
 * @author Priscilla Masunyane
 */
public class RedisCacheAccessException extends RuntimeException {

    /**
     * Constructs a new RedisCacheAccessException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public RedisCacheAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new RedisCacheAccessException with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause   the underlying cause of the exception (can be {@code null})
     */
    public RedisCacheAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
