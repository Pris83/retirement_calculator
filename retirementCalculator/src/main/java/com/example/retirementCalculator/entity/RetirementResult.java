package com.example.retirementCalculator.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

/**
 * DTO representing the results of a retirement plan calculation.
 * <p>
 * This object is returned by the service layer and sent to clients
 * via the REST API, containing all computed values and original input.
 * </p>
 *
 * <p>Example output:</p>
 * <pre>
 * {
 *   "currentAge": 30,
 *   "retirementAge": 65,
 *   "interestRate": 5.0,
 *   "lifestyleType": "simple",
 *   "monthlyDeposit": 1000.0,
 *   "futureValue": 1140826.14
 * }
 * </pre>
 *
 * @author Priscilla Masunyane
 */

@Data
@NoArgsConstructor
public class RetirementResult {
    private int currentAge;
    private int retirementAge;
    private double interestRate;
    private String lifestyleType;
    private double monthlyDeposit;
    private double futureValue;

    /**
     * Constructs a new RetirementResult.
     *
     * @param currentAge       current age of the user
     * @param retirementAge    retirement age of the user
     * @param interestRate     interest rate as a percentage
     * @param lifestyleType    lifestyle type (e.g., simple, fancy)
     * @param monthlyDeposit   deposit amount per month
     * @param futureValue      projected total at retirement
     */
    public RetirementResult(int currentAge, int retirementAge, double interestRate, String lifestyleType, double monthlyDeposit, double futureValue) {
        this.currentAge = currentAge;
        this.retirementAge = retirementAge;
        this.interestRate = interestRate;
        this.lifestyleType = lifestyleType;
        this.monthlyDeposit = monthlyDeposit;
        this.futureValue = futureValue;
    }


    public int getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
    }

    public int getRetirementAge() {
        return retirementAge;
    }

    public void setRetirementAge(int retirementAge) {
        this.retirementAge = retirementAge;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public String getLifestyleType() {
        return lifestyleType;
    }

    public void setLifestyleType(String lifestyleType) {
        this.lifestyleType = lifestyleType;
    }

    public double getMonthlyDeposit() {
        return monthlyDeposit;
    }

    public void setMonthlyDeposit(double monthlyDeposit) {
        this.monthlyDeposit = monthlyDeposit;
    }

    public double getFutureValue() {
        return Math.round(futureValue * 100.0) / 100.0;
    }




}
