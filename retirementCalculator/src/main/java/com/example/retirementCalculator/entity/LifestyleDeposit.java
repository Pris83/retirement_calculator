package com.example.retirementCalculator.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a lifestyle-based monthly deposit configuration used
 * in retirement planning calculations.
 * <p>
 * This table stores predefined monthly contribution values associated with lifestyle types,
 * such as "simple" or "fancy". The values are used by the retirement calculator service
 * to estimate future savings.
 * </p>
 *
 * <p>
 * Example entries:
 * <ul>
 *     <li>simple → 1000.00</li>
 *     <li>fancy → 3000.00</li>
 * </ul>
 * </p>
 *
 * Mapped to a database table using JPA annotations.
 *
 * @author Priscilla Masunyane
 */
@Entity
@Getter
@Setter
public class LifestyleDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String lifestyleType;

    private Double monthlyDeposit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMonthlyDeposit() {
        return monthlyDeposit;
    }

    public void setMonthlyDeposit(Double monthlyDeposit) {
        this.monthlyDeposit = monthlyDeposit;
    }

    public String getLifestyleType() {
        return lifestyleType;
    }

    public void setLifestyleType(String lifestyleType) {
        this.lifestyleType = lifestyleType;
    }


}
