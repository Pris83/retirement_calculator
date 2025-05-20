package com.example.retirementCalculator.exception;

/**
 * Base exception class for all custom exceptions in the Retirement Calculator application.
 * <p>
 * This abstract class extends {@link RuntimeException} and includes an error code
 * to provide more specific information about the type of error encountered.
 * </p>
 *
 * <p>Subclasses should provide meaningful error messages and unique error codes.</p>
 *
 * Example usage:
 * <pre>
 *     throw new SomeSpecificRetirementCalculatorException("Detailed message", "RC-123");
 * </pre>
 *
 * @author Priscilla Masunyane
 */
public abstract class RetirementCalculatorException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructs a new RetirementCalculatorException with the specified detail message
     * and error code.
     *
     * @param message the detailed error message
     * @param errorCode the error code identifying the error type
     */
    public RetirementCalculatorException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new RetirementCalculatorException with the specified detail message,
     * error code, and cause.
     *
     * @param message the detailed error message
     * @param errorCode the error code identifying the error type
     * @param cause the underlying cause of the exception (can be {@code null})
     */
    public RetirementCalculatorException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code as a {@link String}
     */
    public String getErrorCode() {
        return errorCode;
    }
}
