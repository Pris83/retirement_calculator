package com.example.retirementCalculator.entity;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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
@Table(name = "lifestyle_deposits", schema = "retirement_staging")
@XmlRootElement(name = "lifestyleDeposit")
        public class LifestyleDeposit {

    /**
     * The primary key identifier for the lifestyle deposit record.
     * This value is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The type of lifestyle (e.g., "simple", "moderate", "fancy") used to categorize savings goals.
     */
    @Column(name = "lifestyleType")
    private String lifestyleType;

    /**
     * The monthly deposit amount associated with the given lifestyle type.
     */
    @Column(name = "monthlyDeposit")
    private BigDecimal monthlyDeposit;

    public LifestyleDeposit(){}
    public LifestyleDeposit(String lifestyleType, BigDecimal monthlyDeposit) {
        this.lifestyleType = lifestyleType;
        this.monthlyDeposit = monthlyDeposit;
    }
    /**
     * Gets the unique identifier for this record.
     *
     * @return the ID of the lifestyle deposit
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this record.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }



    /**
     * Gets the monthly deposit value associated with this lifestyle.
     *
     * @return the monthly deposit amount
     */
    @XmlElement
    public BigDecimal getMonthlyDeposit() {
        return monthlyDeposit;
    }

    /**
     * Sets the monthly deposit value.
     *
     * @param monthlyDeposit the monthly deposit to set
     */
    public void setMonthlyDeposit(BigDecimal monthlyDeposit) {
        this.monthlyDeposit = monthlyDeposit;
    }

    /**
     * Gets the lifestyle type label.
     *
     * @return the lifestyle type (e.g., "simple", "fancy")
     */
    @XmlElement
    public String getLifestyleType() {
        return lifestyleType;
    }

    /**
     * Sets the lifestyle type.
     *
     * @param lifestyleType the lifestyle type to set
     */
    public void setLifestyleType(String lifestyleType) {
        this.lifestyleType = lifestyleType;
    }

}
