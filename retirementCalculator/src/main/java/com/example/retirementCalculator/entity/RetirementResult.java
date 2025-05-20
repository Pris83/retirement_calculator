package com.example.retirementCalculator.entity;

import jakarta.persistence.Entity;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

import java.math.BigDecimal;

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
@Builder
public class RetirementResult {

    /**
     * The user's current age at the time of calculation.
     */
    private int currentAge;

    /**
     * The target retirement age.
     */
    private int retirementAge;

    /**
     * Expected annual interest rate used for compounding.
     */
    private double interestRate;

    /**
     * The user's selected lifestyle type (e.g., "simple" or "fancy").
     */
    private String lifestyleType;

    /**
     * The monthly amount deposited toward retirement savings.
     */
    private BigDecimal monthlyDeposit;

    /**
     * The total future value of the retirement fund at retirement age.
     */
    private BigDecimal futureValue;

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
    public RetirementResult(int currentAge, int retirementAge, double interestRate, String lifestyleType,
                            BigDecimal monthlyDeposit, BigDecimal futureValue) {
        this.currentAge = currentAge;
        this.retirementAge = retirementAge;
        this.interestRate = interestRate;
        this.lifestyleType = lifestyleType;
        this.monthlyDeposit = monthlyDeposit;
        this.futureValue = futureValue;
    }

    /**
     * Gets the current age of the user.
     */
    public int getCurrentAge() {
        return currentAge;
    }

    /**
     * Sets the current age of the user.
     */
    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
    }

    /**
     * Gets the planned retirement age.
     */
    public int getRetirementAge() {
        return retirementAge;
    }

    /**
     * Sets the planned retirement age.
     */
    public void setRetirementAge(int retirementAge) {
        this.retirementAge = retirementAge;
    }

    /**
     * Gets the expected annual interest rate.
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Sets the expected annual interest rate.
     */
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * Gets the lifestyle type chosen by the user.
     */
    public String getLifestyleType() {
        return lifestyleType;
    }

    /**
     * Sets the lifestyle type (e.g., simple, fancy).
     */
    public void setLifestyleType(String lifestyleType) {
        this.lifestyleType = lifestyleType;
    }

    /**
     * Gets the monthly deposit amount.
     */
    public BigDecimal getMonthlyDeposit() {
        return monthlyDeposit;
    }

    /**
     * Sets the monthly deposit amount.
     */
    public void setMonthlyDeposit(BigDecimal monthlyDeposit) {
        this.monthlyDeposit = monthlyDeposit;
    }

    /**
     * Gets the projected future value of retirement savings.
     */
    public BigDecimal getFutureValue() {
        return futureValue;
    }

    /**
     * Sets the projected future value of retirement savings.
     */
    public void setFutureValue(BigDecimal futureValue) {
        this.futureValue = futureValue;
    }
}
