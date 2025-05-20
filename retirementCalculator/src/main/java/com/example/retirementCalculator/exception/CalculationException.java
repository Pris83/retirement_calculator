package com.example.retirementCalculator.exception;

/**
 * Exception thrown when an error occurs during retirement plan calculation.
 * <p>
 * This exception extends {@link RetirementCalculatorException} and
 * represents failures such as invalid calculations or unexpected
 * runtime issues during the computation of retirement projections.
 * </p>
 * <p>
 * The error code "RC-500" is used to signify an internal calculation error.
 * </p>
 *
 * @author Priscilla Masunyane
 */
public class CalculationException extends RetirementCalculatorException {

    /**
     * Constructs a new CalculationException with the specified detail message.
     * The message is prefixed for clarity, and a fixed error code "RC-500" is set.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public CalculationException(String message) {
        super("Error during retirement calculation: " + message, "RC-500");
    }
}
