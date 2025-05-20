package com.example.retirementCalculator.exception;

/**
 * Exception thrown when an error occurs while updating an entry in the Redis cache.
 * <p>
 * This exception indicates a failure during the cache update operation,
 * such as connection issues or Redis command failures.
 * </p>
 *
 * Example usage:
 * <pre>
 *     throw new RedisCacheUpdateException("Failed to update cache key: " + key);
 * </pre>
 *
 * This exception extends {@link RuntimeException} and supports optional cause chaining.
 *
 * @author Priscilla Masunyane
 */
public class RedisCacheUpdateException extends RuntimeException {

    /**
     * Constructs a new RedisCacheUpdateException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public RedisCacheUpdateException(String message) {
        super(message);
    }

    /**
     * Constructs a new RedisCacheUpdateException with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the underlying cause of the exception (can be {@code null})
     */
    public RedisCacheUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
