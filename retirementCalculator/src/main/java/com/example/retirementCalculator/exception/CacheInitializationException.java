package com.example.retirementCalculator.exception;

/**
 * Exception thrown when cache initialization fails.
 * <p>
 * This unchecked exception indicates issues during cache setup or configuration,
 * such as inability to connect to the caching infrastructure.
 * </p>
 *
 * @author Priscilla Masunyane
 */
public class CacheInitializationException extends RuntimeException {

    /**
     * Constructs a new CacheInitializationException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public CacheInitializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new CacheInitializationException with the specified detail message and cause.
     *
     * @param message the detail message explaining the cause of the exception
     * @param cause   the underlying cause of this exception
     */
    public CacheInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
