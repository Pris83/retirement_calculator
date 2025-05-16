package com.example.retirementCalculator.exception;

public class CalculationException extends RetirementCalculatorException {

    public CalculationException(String message) {
        super("Error during retirement calculation: " + message, "RC-500");
    }
}
