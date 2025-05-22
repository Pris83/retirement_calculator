package com.example.retirementCalculator.service;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import com.example.retirementCalculator.exception.*;
import com.example.retirementCalculator.repository.RetirementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service to manage caching operations for retirement plan data,
 * specifically lifestyle deposit values stored in Redis cache.
 * <p>
 * Supports querying cache status, refreshing cache entries from database,
 * updating and deleting cache keys.
 * </p>
 * <p>
 * Uses {@link StringRedisTemplate} to interact with Redis and
 * {@link RetirementRepository} to fetch persistent lifestyle deposit data.
 * </p>
 *
 * @author Priscilla Masunyane
 */
@Service
public class CacheService {

    private final CacheManager cacheManager;

    @Qualifier("redisTemplateDb0")
    private final StringRedisTemplate redisTemplate;
    @Qualifier("redisTemplateDb1")
    private final StringRedisTemplate redisSecondTemplate;
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    private final RetirementService retirementService;
    private final RetirementRepository retirementRepository;

    @Autowired
    public CacheService(CacheManager cacheManager, @Qualifier("stringRedisTemplateDb0") StringRedisTemplate redisTemplate, @Qualifier("stringRedisTemplateDb1") StringRedisTemplate redisSecondTemplate, RetirementService retirementService, RetirementRepository retirementRepository) {
        this.cacheManager = cacheManager;
        this.redisTemplate = redisTemplate;
        this.redisSecondTemplate = redisSecondTemplate;
        this.retirementService = retirementService;
        this.retirementRepository = retirementRepository;
    }

    /**
     * Checks the status of the cache for the given key.
     * <p>
     * If Redis is connected, returns info about whether the key exists and its approximate size.
     * If Redis is down or unavailable, attempts to report fallback cache status.
     * </p>
     *
     * @param key the cache key to check
     * @return status string describing the cache state and key presence
     */
    public String getCacheStatus(String key) {
        try {
            if (redisTemplate.getConnectionFactory() != null) {
                // Check if Redis connection is alive
                Boolean isRedisUp = redisTemplate.getConnectionFactory().getConnection().ping() != null;

                if (Boolean.TRUE.equals(isRedisUp)) {
                    // Check if key exists and approximate size in Redis
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


    /**
     * Refreshes the cache entry for the given lifestyle type key.
     * <p>
     * Deletes the current cache entry and reloads the value from the database.
     * Stores the new value in Redis as a formatted string.
     * </p>
     *
     * @param key the lifestyle type key for which to refresh the cache
     * @return message indicating success or error details
     * @throws CacheUpdateException if cache update fails
     */
    public String refreshCache(String key) {
        try {
            redisTemplate.delete(key);

            LifestyleDeposit freshValueFromDb = retirementRepository.findByLifestyleType(key)
                    .orElseThrow(() -> new EntityNotFoundException("Deposit not found for key: " + key));

            String valueAsString = "LifestyleType: " + freshValueFromDb.getLifestyleType()
                    + ", Amount: " + freshValueFromDb.getMonthlyDeposit();

            // Store refreshed value in Redis cache
            redisTemplate.opsForValue().set(key, valueAsString);
            return "Cache refreshed for key: " + key + " with value: " + valueAsString;
        } catch (CacheUpdateException e) {
            throw new CacheUpdateException("Cache update failed");
        } catch (Exception e) {
            return "Error refreshing cache for key: " + key + " - " + e.getMessage();
        }
    }

    /**
     * Refreshes all cache entries related to lifestyle deposits.
     * <p>
     * Clears all lifestyle deposit keys from Redis, then fetches all lifestyle deposits
     * from the database and caches them again.
     * </p>
     *
     * @return message indicating the result of the operation
     */
    public String refreshAllCache() {
        try {
            // Delete all keys (consider prefixing keys for safer deletion in production)
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }

            // Fetch all deposits from DB and recache
            List<LifestyleDeposit> allDeposits = retirementRepository.findAll();

            if (allDeposits.isEmpty()) {
                return "No LifestyleDeposit records found in the database.";
            }

            for (LifestyleDeposit deposit : allDeposits) {
                String key = deposit.getLifestyleType();
                String valueAsString = "LifestyleType: " + deposit.getLifestyleType()
                        + ", Amount: " + deposit.getMonthlyDeposit();

                redisTemplate.opsForValue().set(key, valueAsString);
            }

            return "Cache successfully refreshed for all LifestyleDeposit entries.";
        } catch (CacheUpdateException e) {
            throw new CacheUpdateException("Cache update failed");
        } catch (Exception e) {
            return "Error refreshing all cache entries: " + e.getMessage();
        }
    }

    /**
     * Fetches cached data for the given key.
     *
     * @param key the cache key to fetch
     * @return cached string data if present; null otherwise
     * @throws RedisCacheAccessException if cache access fails
     */
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


    public Map<String, String> fetchAllCache() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys == null || keys.isEmpty()) {
                log.warn("No keys found in Redis cache.");
                return Collections.emptyMap();
            }

            List<String> depositValues = redisTemplate.opsForValue().multiGet(keys);
            List<String> interestValues = redisSecondTemplate.opsForValue().multiGet(keys);

            Map<String, String> cacheData = new HashMap<>();
            int i = 0;
            for (String key : keys) {
                String depositValue = depositValues != null ? depositValues.get(i) : null;
                String interestValue = interestValues != null ? interestValues.get(i) : null;

                cacheData.put(key + ":deposit", depositValue);
                cacheData.put(key + ":interest", interestValue);
                i++;
            }

            log.info("Fetched {} entries from Redis cache.", cacheData.size());
            return cacheData;

        } catch (Exception e) {
            log.error("Error fetching all data from Redis cache", e);
            return Collections.emptyMap();
        }
    }


    /**
     * Updates the cache for the given key with the specified value.
     *
     * @param key   the cache key to update
     * @param value the value to set in the cache
     * @throws RedisCacheUpdateException if cache update fails
     */
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

    /**
     * Deletes the cache entry for the given key.
     *
     * @param key the cache key to delete
     */
    public void deleteFromCache(String key) {
        try {
            log.info("Deleting cache for key: {}", key);
            redisTemplate.delete(key);
            log.debug("Cache deleted for key: {}", key);
        } catch (RedisCacheDeleteException e) {
            log.error("Error deleting cache for key {}: {}", key, e.getMessage());
       throw  new RedisCacheDeleteException("Error deleting cache for key" + key);
        } catch (Exception e) {
            log.error("Unexpected error deleting cache for key {}: {}", key, e.getMessage());
        }
    }


}
