package com.example.retirementCalculator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CacheServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private Cache cache;

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGetCacheStatus_RedisCache() {
        RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);
        RedisConnection connection = mock(RedisConnection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.ping()).thenReturn("PONG");

        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(redisTemplate.opsForValue().size("cacheName")).thenReturn(10L);

        String status = cacheService.getCacheStatus("cacheName");

        assertEquals("Redis is UP | Cache key cacheName does NOT exist | Approximate size: 10", status);
    }


    @Test
    void testGetCacheStatus_CacheManagerCache() {
        when(redisTemplate.getConnectionFactory()).thenReturn(null);
        when(cacheManager.getCache("cacheName")).thenReturn(cache);

        String status = cacheService.getCacheStatus("cacheName");

        assertEquals("Cache is initialized (non-Redis), but size is unknown.", status);
    }

    @Test
    void testGetCacheStatus_NoCache() {
        when(redisTemplate.getConnectionFactory()).thenReturn(null);
        when(cacheManager.getCache("cacheName")).thenReturn(null);

        String status = cacheService.getCacheStatus("cacheName");

        assertEquals("Cache is NOT initialized (no Redis connection or fallback cache).", status);
    }


    @Test
    void testRefreshCache_Success() {
        when(cacheManager.getCache("cacheName")).thenReturn(cache);

        cacheService.refreshCache("key");

        verify(cache).evict("key");
        verify(cache).put("key", "New Value for key");
    }


    @Test
    void testRefreshCache_NoCache() {
        when(cacheManager.getCache("cacheName")).thenReturn(null);

        String newValue = cacheService.refreshCache("key");

        assertNull(newValue);
    }


    @Test
    void testFetchFromCache_Found() {
        when(valueOperations.get("key")).thenReturn("cachedValue");

        String cachedData = cacheService.fetchFromCache("key");

        assertEquals("cachedValue", cachedData);
        verify(valueOperations).get("key");
    }

    @Test
    void testFetchFromCache_NotFound() {
        when(valueOperations.get("key")).thenReturn(null);

        String cachedData = cacheService.fetchFromCache("key");

        assertNull(cachedData);
        verify(valueOperations).get("key");
    }

    @Test
    void testUpdateCache() {
        doNothing().when(valueOperations).set("key", "newValue");

        cacheService.updateCache("key", "newValue");

        verify(valueOperations).set("key", "newValue");
    }

    @Test
    void testDeleteFromCache() {
        cacheService.deleteFromCache("key");

        verify(redisTemplate).delete("key");
    }
}
