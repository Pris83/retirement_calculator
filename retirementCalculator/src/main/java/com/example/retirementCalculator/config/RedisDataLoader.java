package com.example.retirementCalculator.config;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import com.example.retirementCalculator.repository.RetirementRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for loading lifestyle deposit data from the database
 * into Redis at application startup.
 */
@Component
@Profile("!test")
public class RedisDataLoader {

    private final RetirementRepository repository;
    private final StringRedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RedisDataLoader.class);

    /**
     * Constructs a new {@code RedisDataLoader} with the required dependencies.
     *
     * @param repository    the repository used to fetch lifestyle deposit data
     * @param redisTemplate the Redis template used to interact with Redis
     */
    @Autowired
    public RedisDataLoader(RetirementRepository repository, StringRedisTemplate redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Loads lifestyle deposit data into Redis after the application context is initialized.
     * Each entry is stored with the lifestyle type (in lowercase) as the key and the monthly deposit as the value.
     */
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
