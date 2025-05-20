package com.example.retirementCalculator.exception;

/**
 * Exception thrown when an error occurs while deleting an entry from the Redis cache.
 * <p>
 * This exception indicates a failure during the removal of a cache key/value,
 * which might be caused by connection problems or Redis command failures.
 * </p>
 *
 * Example usage:
 * <pre>
 *     throw new RedisCacheDeleteException("Failed to delete cache key: " + key);
 * </pre>
 *
 * This exception extends {@link RuntimeException} and supports optional cause chaining.
 *
 * @author Priscilla Masunyane
 */
public class RedisCacheDeleteException extends RuntimeException {

    /**
     * Constructs a new RedisCacheDeleteException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public RedisCacheDeleteException(String message) {
        super(message);
    }

    /**
     * Constructs a new RedisCacheDeleteException with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the underlying cause of the exception (can be {@code null})
     */
    public RedisCacheDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
