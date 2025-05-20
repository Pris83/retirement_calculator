package com.example.retirementCalculator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.xml.bind.annotation.*;
import lombok.*;
import jakarta.validation.constraints.*;

/**
 * Entity representing user-provided retirement plan input.
 * <p>
 * This entity captures the essential data needed to calculate
 * the future value of a retirement savings plan based on current age,
 * retirement age, and expected interest rate.
 * </p>
 *
 * <p>
 * Validation constraints are applied to ensure meaningful and valid input.
 * </p>
 *
 * Example JSON input:
 * <pre>
 * {
 *   "currentAge": 30,
 *   "retirementAge": 65,
 *   "interestRate": 5.0,
 *   "lifestyleType": "simple"
 * }
 * </pre>
 *
 * @author Priscilla Masunyane
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder

        public class Retirement {

    /**
     * The user's current age.
     * Must be 0 or greater.
     */
    @NotNull(message = "Current age cannot be null")
    @Min(value = 0, message = "Current age must be 0 or older")
    @XmlElement(name = "currentAge")
    private Integer currentAge;

    /**
     * The age at which the user plans to retire.
     * Must be greater than 0.
     */
    @NotNull(message = "Retirement age cannot be null")
    @Min(value = 1, message = "Retirement age must be greater than 0")
    private Integer retirementAge;

    /**
     * Expected annual interest rate (as a percentage).
     * Must be greater than 0.0.
     */
    @NotNull(message = "Interest rate cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Interest rate must be non-negative")
    private Double interestRate;

    /**
     * Type of lifestyle selected by the user, which influences savings strategy.
     * Allowed values: "simple" or "fancy" (case-insensitive).
     */
    @NotNull(message = "Lifestyle type cannot be null")
    @Pattern(regexp = "simple|fancy", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Lifestyle type must be 'simple' or 'fancy'")
    private String lifestyleType;

    /**
     * Returns the user's current age.
     */
    public Integer getCurrentAge() {
        return currentAge;
    }

    /**
     * Sets the user's current age.
     */
    public void setCurrentAge(Integer currentAge) {
        this.currentAge = currentAge;
    }

    /**
     * Returns the target retirement age.
     */
    public Integer getRetirementAge() {
        return retirementAge;
    }

    /**
     * Sets the target retirement age.
     */
    public void setRetirementAge(Integer retirementAge) {
        this.retirementAge = retirementAge;
    }

    /**
     * Returns the expected annual interest rate.
     */
    public Double getInterestRate() {
        return interestRate;
    }

    /**
     * Sets the expected annual interest rate.
     */
    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * Returns the lifestyle type chosen by the user.
     */
    public String getLifestyleType() {
        return lifestyleType;
    }

    /**
     * Sets the lifestyle type (must be 'simple' or 'fancy').
     */
    public void setLifestyleType(String lifestyleType) {
        this.lifestyleType = lifestyleType;
    }
}
