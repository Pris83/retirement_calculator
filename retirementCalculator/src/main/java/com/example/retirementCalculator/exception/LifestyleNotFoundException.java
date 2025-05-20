package com.example.retirementCalculator.exception;

/**
 * Exception thrown when a specified lifestyle type does not have a configured deposit amount.
 * <p>
 * This exception indicates that the requested lifestyle type could not be found in
 * the system or cache, typically during retirement calculation.
 * </p>
 *
 * Example usage:
 * <pre>
 *   throw new LifestyleNotFoundException("fancy");
 * </pre>
 *
 * This exception carries the error code {@code RC-404} to indicate a not-found scenario.
 *
 * @author Priscilla Masunyane
 */
public class LifestyleNotFoundException extends RetirementCalculatorException {

    /**
     * Constructs a new LifestyleNotFoundException with a detailed message and error code.
     *
     * @param lifestyleType the lifestyle type that was not found
     */
    public LifestyleNotFoundException(String lifestyleType) {
        super("No deposit amount configured for lifestyle type: " + lifestyleType, "RC-404");
    }
}
