package com.example.retirementCalculator.exception;

public class LifestyleNotFoundException extends RetirementCalculatorException {

    public LifestyleNotFoundException(String lifestyleType) {
        super("No deposit amount configured for lifestyle type: " + lifestyleType, "RC-404");
    }
}
