package com.example.retirementCalculator.exception;

/**
 * Exception thrown when a failure occurs while attempting to connect to Redis.
 * <p>
 * This exception indicates that the application was unable to establish
 * or maintain a connection with the Redis server.
 * </p>
 *
 * Example usage:
 * <pre>
 *     throw new RedisConnectionFailureException("Failed to connect to Redis server");
 * </pre>
 *
 * This exception extends {@link RuntimeException} and supports optional cause chaining.
 *
 * @author Priscilla Masunyane
 */
public class RedisConnectionFailureException extends RuntimeException {

    /**
     * Constructs a new RedisConnectionFailureException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the connection failure
     */
    public RedisConnectionFailureException(String message) {
        super(message);
    }

    /**
     * Constructs a new RedisConnectionFailureException with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the cause of the connection failure
     * @param cause the underlying cause of the exception (can be {@code null})
     */
    public RedisConnectionFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
