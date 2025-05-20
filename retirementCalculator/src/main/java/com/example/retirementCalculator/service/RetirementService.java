package com.example.retirementCalculator.service;

import com.example.retirementCalculator.entity.Retirement;
import com.example.retirementCalculator.entity.RetirementResult;
import com.example.retirementCalculator.exception.CalculationException;
import com.example.retirementCalculator.exception.InvalidInputException;
import com.example.retirementCalculator.exception.LifestyleNotFoundException;
import com.example.retirementCalculator.repository.RetirementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service responsible for performing retirement plan calculations.
 * <p>
 * Uses lifestyle deposit data cached in Redis to calculate the future value
 * of monthly deposits made until retirement age, considering an interest rate.
 * </p>
 * <p>
 * Validates input parameters and handles exceptions related to missing lifestyle deposits
 * or invalid inputs.
 * </p>
 *
 * @author Priscilla Masunyane
 */
@Service
public class RetirementService {

    private final StringRedisTemplate redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(RetirementService.class);
    private final RetirementRepository retirementRepository;

    @Autowired
    public RetirementService(StringRedisTemplate redisTemplate, RetirementRepository retirementRepository) {
        this.redisTemplate = redisTemplate;
        this.retirementRepository = retirementRepository;
    }

    /**
     * Calculates the retirement plan results based on user input and cached lifestyle deposit values.
     * <p>
     * The calculation estimates the future value of monthly deposits made until retirement age
     * using compound interest, where the monthly deposit amount is retrieved from Redis cache
     * based on the lifestyle type.
     * </p>
     *
     * @param dto the {@link Retirement} DTO containing user input such as current age, retirement age,
     *            interest rate, and lifestyle type
     * @return a {@link RetirementResult} object containing the calculation results and input data
     * @throws InvalidInputException      if any of the input values are invalid (negative ages, interest rate, etc.)
     * @throws LifestyleNotFoundException if the lifestyle type deposit amount is not found in Redis cache
     * @throws CalculationException       if an unexpected error occurs during calculation
     */
    public RetirementResult calculatePlan(Retirement dto) {
        log.info("Starting retirement plan calculation for lifestyle type: {}", dto.getLifestyleType());

        // Input validation
        if (dto.getCurrentAge() < 0) {
            throw new InvalidInputException("currentAge", "must be non-negative");
        }
        if (dto.getRetirementAge() <= dto.getCurrentAge()) {
            throw new InvalidInputException("retirementAge", "must be greater than currentAge");
        }
        if (dto.getInterestRate() < 0.0) {
            throw new InvalidInputException("interestRate", "must be non-negative");
        }

        try {
            String depositStr = redisTemplate.opsForValue().get(dto.getLifestyleType().toLowerCase());

            if (depositStr == null) {
                log.error("No deposit amount found in Redis for lifestyle type: {}", dto.getLifestyleType());
                throw new LifestyleNotFoundException(dto.getLifestyleType());
            }

            BigDecimal monthlyDeposit = new BigDecimal(depositStr);
            log.debug("Retrieved monthly deposit from Redis: {}", monthlyDeposit);

            int months = (dto.getRetirementAge() - dto.getCurrentAge()) * 12;
            BigDecimal monthlyInterestRate = BigDecimal.valueOf(dto.getInterestRate())
                    .divide(BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);

//            BigDecimal futureValue = BigDecimal.ZERO;

            BigDecimal futureValue;

            if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
                // No interest case
                futureValue = monthlyDeposit.multiply(BigDecimal.valueOf(months));
            } else {
                // Compound interest case
                BigDecimal onePlusRPowerN = (BigDecimal.ONE.add(monthlyInterestRate)).pow(months);
                futureValue = monthlyDeposit.multiply(onePlusRPowerN.subtract(BigDecimal.ONE))
                        .divide(monthlyInterestRate, 10, RoundingMode.HALF_UP);
            }


            futureValue = futureValue.setScale(2, RoundingMode.HALF_UP);
            log.info("Calculated future value: {}", futureValue);

            RetirementResult result = new RetirementResult(
                    dto.getCurrentAge(),
                    dto.getRetirementAge(),
                    dto.getInterestRate(),
                    dto.getLifestyleType(),
                    monthlyDeposit,
                    futureValue
            );

            log.debug("Retirement result constructed: {}", result);
            return result;

        } catch (LifestyleNotFoundException | InvalidInputException e) {
            throw e; // Propagate known exceptions
        } catch (Exception e) {
            log.error("Unexpected error during calculation", e);
            throw new CalculationException("Unexpected error during retirement calculation");
        }
    }
}
