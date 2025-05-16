package com.example.retirementCalculator.exception;

public class DatabaseAccessException extends RetirementCalculatorException {

    public DatabaseAccessException(String message, Throwable cause) {
        super("Database access error: " + message, "RC-503", cause);
    }
}
