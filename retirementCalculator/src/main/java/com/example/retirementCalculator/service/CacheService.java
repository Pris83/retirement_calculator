package com.example.retirementCalculator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    private final StringRedisTemplate redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    public CacheService(CacheManager cacheManager, StringRedisTemplate redisTemplate) {
        this.cacheManager = cacheManager;
        this.redisTemplate = redisTemplate;
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
        } catch (Exception e) {
            return "Error checking cache status: " + e.getMessage();
        }
    }


    // Refresh cache for a specific key
    public String refreshCache(String key) {
        Cache cache = cacheManager.getCache("cacheName");
        if (cache == null) {
            return null;
        }

        cache.evict(key);

        String newValue = "New Value for " + key;

        // Put the new value in the cache
        cache.put(key, newValue);

        return newValue;
    }


    public String fetchFromCache(String key) {
        log.info("Fetching data from cache for key: {}", key);
        String cachedData = redisTemplate.opsForValue().get(key);

        if (cachedData == null) {
            log.warn("No data found in cache for key: {}", key);
        } else {
            log.debug("Found data in cache for key: {}: {}", key, cachedData);
        }

        return cachedData;
    }


    public void updateCache(String key, String value) {
        log.info("Updating cache for key: {} with value: {}", key, value);
        redisTemplate.opsForValue().set(key, value);
        log.debug("Cache updated for key: {}", key);
    }


    public void deleteFromCache(String key) {
        log.info("Deleting cache for key: {}", key);
        redisTemplate.delete(key);
        log.debug("Cache deleted for key: {}", key);
    }
}
