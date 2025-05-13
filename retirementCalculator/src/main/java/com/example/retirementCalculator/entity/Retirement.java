package com.example.retirementCalculator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotNull;

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
 *   "interestRate": 5.0
 * }
 * </pre>
 *
 * Note: Lifestyle type may be handled separately depending on design.
 *
 * @author Priscilla Masunyane
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Retirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Current age cannot be null")
    @Min(value = 0, message = "Current age must be 0 or older")
    private Integer currentAge;

    @NotNull(message = "Retirement age cannot be null")
    @Min(value = 1, message = "Retirement age must be greater than 0")
    private Integer retirementAge;

    @NotNull(message = "Interest rate cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be positive")
    private Double interestRate;

    @NotNull(message = "Lifestyle type cannot be null")
    @Pattern(regexp = "simple|fancy", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Lifestyle type must be 'simple' or 'fancy'")
    private String lifestyleType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "Current age cannot be null") @Min(value = 0, message = "Current age must be 0 or older") Integer getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(@NotNull(message = "Current age cannot be null") @Min(value = 0, message = "Current age must be 0 or older") Integer currentAge) {
        this.currentAge = currentAge;
    }

    public @NotNull(message = "Retirement age cannot be null") @Min(value = 1, message = "Retirement age must be greater than 0") Integer getRetirementAge() {
        return retirementAge;
    }

    public void setRetirementAge(@NotNull(message = "Retirement age cannot be null") @Min(value = 1, message = "Retirement age must be greater than 0") Integer retirementAge) {
        this.retirementAge = retirementAge;
    }

    public @NotNull(message = "Interest rate cannot be null") @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be positive") Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(@NotNull(message = "Interest rate cannot be null") @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be positive") Double interestRate) {
        this.interestRate = interestRate;
    }

    public @NotNull(message = "Lifestyle type cannot be null") @Pattern(regexp = "simple|fancy", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Lifestyle type must be 'simple' or 'fancy'") String getLifestyleType() {
        return lifestyleType;
    }

    public void setLifestyleType(@NotNull(message = "Lifestyle type cannot be null") @Pattern(regexp = "simple|fancy", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Lifestyle type must be 'simple' or 'fancy'") String lifestyleType) {
        this.lifestyleType = lifestyleType;
    }

}

