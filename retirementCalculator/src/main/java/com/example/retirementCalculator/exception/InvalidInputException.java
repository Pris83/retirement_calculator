package com.example.retirementCalculator.exception;

/**
 * Exception thrown when an input parameter is invalid.
 * <p>
 * Indicates that a user or client has provided input that does not meet
 * the required validation criteria for a specific field.
 * </p>
 *
 * Example:
 * <pre>
 *   throw new InvalidInputException("currentAge", "must be non-negative");
 * </pre>
 *
 * This exception carries a standardized error code {@code RC-400} for easy identification.
 *
 * @author Priscilla Masunyane
 */
public class InvalidInputException extends RetirementCalculatorException {

    /**
     * Constructs a new InvalidInputException with a detailed message and error code.
     *
     * @param field  the name of the input field that is invalid
     * @param reason the reason why the input is invalid
     */
    public InvalidInputException(String field, String reason) {
        super("Invalid input: " + field + " - " + reason, "RC-400");
    }
}
