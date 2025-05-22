package com.example.retirementCalculator.config;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import com.example.retirementCalculator.repository.RetirementRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.opencsv.CSVReader;

/**
 * Loads lifestyle deposit data into Redis cache after Spring context initialization.
 * <p>
 * Loads data from the database and the CSV file.
 * Database data is cached as key-value pairs: lifestyle type (lowercased) as key, monthly deposit as value.
 * CSV data is cached as key-value pairs: lifestyle type (lowercased) as key, interest rate as value.
 */
@Component
@Profile("!test")
public class RedisDataLoader {

    private final RetirementRepository repository;
    @Qualifier("redisTemplateDb0")
    private final StringRedisTemplate redisTemplate;
    @Qualifier("redisTemplateDb1")
    private final StringRedisTemplate  redisSecondTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RedisDataLoader.class);

    /**
     * Constructs a new {@code RedisDataLoader} with the required dependencies.
     *
     * @param repository    the repository to fetch lifestyle deposit data from the database
     * @param redisTemplate the Redis template to interact with the Redis data store
     */
    @Autowired
    public RedisDataLoader(RetirementRepository repository,
                           @Qualifier("stringRedisTemplateDb0") StringRedisTemplate redisTemplate,
                           @Qualifier("stringRedisTemplateDb1") StringRedisTemplate redisSecondTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.redisSecondTemplate = redisSecondTemplate;
    }


    /**
     * Loads lifestyle deposit data into Redis cache after the Spring context initialization.
     * <p>
     * This method triggers loading data both from the database and a CSV file.
     * The database data is stored as simple key-value pairs where the key is the lifestyle type in lowercase,
     * and the value is the monthly deposit as a string.
     * <p>
     * The CSV data is read from the provided file path and stored as Redis hashes,
     * where each hash key is "record:<id>" and fields are mapped from the CSV columns.
     */
    @PostConstruct
    public void loadDataToRedis() {
        loadFromDB();
        loadFromCSV("C:/Upskill/retirement-calculator/retirementCalculator/src/main/resources/lifestyleTypeInterestRate.csv");
    }

    /**
     * Loads lifestyle deposit data from the database and caches it in Redis as key-value pairs.
     * The lifestyle type (lowercased) is used as the Redis key and the monthly deposit as the value.
     */
    private void loadFromDB() {
        List<LifestyleDeposit> deposits = repository.findAll();
        for (LifestyleDeposit deposit : deposits) {
            redisTemplate.opsForValue().set(
                    deposit.getLifestyleType().toLowerCase(),
                    String.valueOf(deposit.getMonthlyDeposit())
            );
            logger.info("Cached: {} => {}", deposit.getLifestyleType(), deposit.getMonthlyDeposit());
        }
    }

    /**
     * Loads lifestyle deposit data from a CSV file and caches it in Redis as key-value pairs.
     * <p>
     * The CSV file is expected to have a header row with at least the columns
     * 'lifestyleType' and 'interestRate'. Each subsequent row is stored in Redis
     * with the lifestyle type (lowercased) as the key and the interest rate as the value.
     *
     * @param csvFilePath the path to the CSV file to load data from
     */
    private void loadFromCSV(String csvFilePath) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] header = reader.readNext();
            String[] line;

            while ((line = reader.readNext()) != null) {
                Map<String, String> dataMap = new HashMap<>();
                for (int i = 0; i < header.length; i++) {
                    dataMap.put(header[i], line[i]);
                }

                String key = dataMap.get("lifestyleType").toLowerCase();

                redisSecondTemplate.opsForValue().set(key, dataMap.get("interestRate"));

                logger.info("Cached from CSV: {} => {}", key, dataMap.get("interestRate"));
            }

            logger.info("CSV data loaded to Redis successfully.");

        } catch (Exception e) {
            logger.error("Error loading CSV data to Redis", e);
        }
    }

}
