package com.example.retirementCalculator.config;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import com.example.retirementCalculator.repository.RetirementRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisDataLoader {

    private final RetirementRepository repository;
    private final StringRedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RedisDataLoader.class);

    @Autowired
    public RedisDataLoader(RetirementRepository repository, StringRedisTemplate redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void loadDataToRedis() {
        List<LifestyleDeposit> deposits = repository.findAll();
        for (LifestyleDeposit deposit : deposits) {
            redisTemplate.opsForValue().set(
                    deposit.getLifestyleType().toLowerCase(),
                    String.valueOf(deposit.getMonthlyDeposit())
            );

        logger.info("Cached: {} => {}", deposit.getLifestyleType(), deposit.getMonthlyDeposit());
        }
    }
}

