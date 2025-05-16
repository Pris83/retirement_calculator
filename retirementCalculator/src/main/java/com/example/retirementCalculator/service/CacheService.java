package com.example.retirementCalculator.service;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import com.example.retirementCalculator.exception.*;
import com.example.retirementCalculator.repository.RetirementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    private final StringRedisTemplate redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    private final RetirementService retirementService;
    private final RetirementRepository retirementRepository;

    @Autowired
    public CacheService(CacheManager cacheManager, StringRedisTemplate redisTemplate, RetirementService retirementService, RetirementRepository retirementRepository) {
        this.cacheManager = cacheManager;
        this.redisTemplate = redisTemplate;
        this.retirementService = retirementService;
        this.retirementRepository = retirementRepository;
    }

    public String getCacheStatus(String key) {
        try {
            if (redisTemplate.getConnectionFactory() != null) {
                // Check if Redis connection is alive
                Boolean isRedisUp = redisTemplate.getConnectionFactory().getConnection().ping() != null;

                if (Boolean.TRUE.equals(isRedisUp)) {
                    // You can also check size, or existence of key(s)
                    Boolean hasKey = redisTemplate.hasKey(key);
                    Long size = redisTemplate.opsForValue().size(key);

                    return "Redis is UP | Cache key " + key +
                            (Boolean.TRUE.equals(hasKey) ? " exists" : " does NOT exist") +
                            " | Approximate size: " + (size != null ? size : 0);
                } else {
                    return "Redis connection exists but is unresponsive (no PONG)";
                }
            } else {
                Cache cache = cacheManager.getCache(key);
                if (cache == null) {
                    return "Cache is NOT initialized (no Redis connection or fallback cache).";
                } else {
                    return "Cache is initialized (non-Redis), but size is unknown.";
                }
            }
        } catch (RedisConnectionFailureException e) {
            return "Error: Redis connection failure - " + e.getMessage();
        } catch (CacheInitializationException e) {
            return "Error: Cache initialization failure - " + e.getMessage();
        } catch (Exception e) {
            return "Error checking cache status: " + e.getMessage();
        }
    }

    // Refresh cache for a specific key
    public String refreshCache(String key) {
        try {
            redisTemplate.delete(key);

            LifestyleDeposit freshValueFromDb = retirementRepository.findByLifestyleType(key)
                    .orElseThrow(() -> new EntityNotFoundException("Deposit not found for key: " + key));

            String valueAsString = "LifestyleType: " + freshValueFromDb.getLifestyleType()
                    + ", Amount: " + freshValueFromDb.getMonthlyDeposit();

            // Step 4: Store string value in Redis
            redisTemplate.opsForValue().set(key, valueAsString);
            return "Cache refreshed for key: " + key + " with value: " + valueAsString;
        }catch(CacheUpdateException e){
            throw new CacheUpdateException("Cache update failed");
        } catch (Exception e) {
            return "Error refreshing cache for key: " + key + " - " + e.getMessage();
        }
    }


    public String refreshAllCache() {
        try {
            // Step 1: Delete all relevant keys from Redis
            Set<String> keys = redisTemplate.keys("*"); // You can scope this more tightly with a prefix like "lifestyle:*"
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }

            // Step 2: Fetch all deposits from the database
            List<LifestyleDeposit> allDeposits = retirementRepository.findAll();

            if (allDeposits.isEmpty()) {
                return "No LifestyleDeposit records found in the database.";
            }

            // Step 3: Re-cache each deposit
            for (LifestyleDeposit deposit : allDeposits) {
                String key = deposit.getLifestyleType(); // You can add a prefix like "lifestyle:" if needed
                String valueAsString = "LifestyleType: " + deposit.getLifestyleType()
                        + ", Amount: " + deposit.getMonthlyDeposit();

                redisTemplate.opsForValue().set(key, valueAsString);
            }

            return "Cache successfully refreshed for all LifestyleDeposit entries.";
        } catch (Exception e) {
            return "Error refreshing all cache entries: " + e.getMessage();
        }
    }

    public String fetchFromCache(String key) {
        try {
            log.info("Fetching data from cache for key: {}", key);
            String cachedData = redisTemplate.opsForValue().get(key);

            if (cachedData == null) {
                log.warn("No data found in cache for key: {}", key);
            } else {
                log.debug("Found data in cache for key: {}: {}", key, cachedData);
            }

            return cachedData;
        } catch (RedisCacheAccessException e) {
            log.error("Error accessing Redis cache: {}", e.getMessage());
            throw new RedisCacheAccessException("Error fetching data from cache");

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return "Unexpected error fetching from cache: " + e.getMessage();
        }
    }


    public void updateCache(String key, String value) {
        try {
            log.info("Updating cache for key: {} with value: {}", key, value);
            redisTemplate.opsForValue().set(key, value);
            log.debug("Cache updated for key: {}", key);
        } catch (RedisCacheUpdateException e) {
            log.error("Error updating cache for key {}: {}", key, e.getMessage());
            throw new RedisCacheUpdateException("Cache update failed");
        } catch (Exception e) {
            log.error("Unexpected error updating cache for key {}: {}", key, e.getMessage());
        }
    }


    public void deleteFromCache(String key) {
        try {
            log.info("Deleting cache for key: {}", key);
            redisTemplate.delete(key);
            log.debug("Cache deleted for key: {}", key);
        } catch (RedisCacheDeleteException e) {
            log.error("Error deleting cache for key {}: {}", key, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error deleting cache for key {}: {}", key, e.getMessage());
        }
    }

}
