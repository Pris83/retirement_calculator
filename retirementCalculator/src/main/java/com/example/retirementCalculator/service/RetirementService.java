package com.example.retirementCalculator.service;

import com.example.retirementCalculator.entity.Retirement;
import com.example.retirementCalculator.entity.RetirementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RetirementService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RetirementService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RetirementResult calculatePlan(Retirement dto) {
        // Fetch deposit amount from Redis
        String depositStr = redisTemplate.opsForValue().get(dto.getLifestyleType().toLowerCase());

        if (depositStr == null) {
            throw new IllegalArgumentException("No deposit amount configured for lifestyle type: " + dto.getLifestyleType());
        }

        double monthlyDeposit = Double.parseDouble(depositStr);

        int months = (dto.getRetirementAge() - dto.getCurrentAge()) * 12;
        double monthlyInterestRate = dto.getInterestRate() / 100 / 12;

        double futureValue = 0;

        for (int i = 0; i < months; i++) {
            futureValue = (futureValue + monthlyDeposit) * (1 + monthlyInterestRate);
        }

        // return result
        return new RetirementResult(
                dto.getCurrentAge(),
                dto.getRetirementAge(),
                dto.getInterestRate(),
                dto.getLifestyleType(),
                monthlyDeposit,
                futureValue
        );
    }
}
