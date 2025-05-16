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

            BigDecimal futureValue = BigDecimal.ZERO;

            for (int i = 0; i < months; i++) {
                futureValue = futureValue.add(monthlyDeposit)
                        .multiply(BigDecimal.ONE.add(monthlyInterestRate));
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
            throw e; // Already handled above
        } catch (Exception e) {
            log.error("Unexpected error during calculation", e);
            throw new CalculationException("Unexpected error during retirement calculation");
        }
    }


}
