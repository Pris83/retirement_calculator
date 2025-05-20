package com.example.retirementCalculator.exception;

/**
 * Exception thrown when an error occurs while accessing the database.
 * <p>
 * This exception extends {@link RetirementCalculatorException} and
 * represents failures related to database connectivity, query execution,
 * or any other database access issue.
 * </p>
 * <p>
 * The error code "RC-503" indicates a database access problem.
 * </p>
 *
 * @author Priscilla Masunyane
 */
public class DatabaseAccessException extends RetirementCalculatorException {

    /**
     * Constructs a new DatabaseAccessException with the specified detail message and cause.
     * The message is prefixed for clarity, and the error code "RC-503" is set.
     *
     * @param message the detail message explaining the cause of the exception
     * @param cause the underlying cause of the exception
     */
    public DatabaseAccessException(String message, Throwable cause) {
        super("Database access error: " + message, "RC-503", cause);
    }
}
