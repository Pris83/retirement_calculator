package com.example.retirementCalculator.service;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import com.example.retirementCalculator.exception.CacheUpdateException;
import com.example.retirementCalculator.exception.RedisCacheAccessException;
import com.example.retirementCalculator.repository.RetirementRepository;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    private RetirementRepository retirementRepository;


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
    void refreshCache_shouldRefreshSuccessfully() {
        // Given
        String key = "simple";
        LifestyleDeposit deposit = new LifestyleDeposit();
        deposit.setLifestyleType("simple");
        deposit.setMonthlyDeposit(BigDecimal.valueOf(1000.0));

        when(retirementRepository.findByLifestyleType(key)).thenReturn(Optional.of(deposit));

        // When
        String result = cacheService.refreshCache(key);

        // Then
        String expectedValue = "LifestyleType: simple, Amount: 1000.0";
        verify(redisTemplate).delete(key);
        verify(valueOperations).set(key, expectedValue);
        assertEquals("Cache refreshed for key: simple with value: " + expectedValue, result);
    }

    @Test
    void refreshCache_shouldReturnErrorIfNotFound() {
        // Given
        String key = "nonexistent";
        when(retirementRepository.findByLifestyleType(key)).thenReturn(Optional.empty());

        // When
        String result = cacheService.refreshCache(key);

        // Then
        assertTrue(result.contains("Error refreshing cache for key: nonexistent"));
        verify(redisTemplate).delete(key); // Still tries to delete
        verify(valueOperations, never()).set(any(), any()); // Should not attempt to cache
    }

    @Test
    void refreshCache_shouldHandleRedisExceptionGracefully() {
        // Given
        String key = "key";
        when(redisTemplate.delete(key)).thenThrow(new RuntimeException("Redis down"));

        // When
        String result = cacheService.refreshCache(key);

        // Then
        assertTrue(result.contains("Error refreshing cache for key: key - Redis down"));
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

    @Test
    void testRefreshCache_ThrowsCacheUpdateException() {
        String key = "test-key";

        doThrow(new CacheUpdateException("Forced failure"))
                .when(redisTemplate).delete(key);

        CacheUpdateException exception = assertThrows(CacheUpdateException.class, () -> {
            cacheService.refreshCache(key);
        });

        assertEquals("Cache update failed", exception.getMessage());
    }


    @Test
    void testFetchFromCache_ThrowsRedisCacheAccessException() {
        when(valueOperations.get("key")).thenThrow(new RedisCacheAccessException("Redis connection error"));

        assertThatThrownBy(() -> cacheService.fetchFromCache("key"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error fetching data from cache");
    }
}
