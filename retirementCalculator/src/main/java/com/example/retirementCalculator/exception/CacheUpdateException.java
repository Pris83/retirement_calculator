package com.example.retirementCalculator.exception;

/**
 * Exception thrown when an error occurs during cache update operations.
 * <p>
 * This unchecked exception indicates a failure while trying to update or refresh
 * cached data, such as write errors or connectivity issues with the cache store.
 * </p>
 *
 * @author Priscilla Masunyane
 */
public class CacheUpdateException extends RuntimeException {

    /**
     * Constructs a new CacheUpdateException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public CacheUpdateException(String message) {
        super(message);
    }

    /**
     * Constructs a new CacheUpdateException with the specified detail message and cause.
     *
     * @param message the detail message explaining the cause of the exception
     * @param cause   the underlying cause of this exception
     */
    public CacheUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
