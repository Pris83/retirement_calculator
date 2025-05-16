package com.example.retirementCalculator.exception;

public class InvalidInputException extends RetirementCalculatorException {

    public InvalidInputException(String field, String reason) {
        super("Invalid input: " + field + " - " + reason, "RC-400");
    }
}
