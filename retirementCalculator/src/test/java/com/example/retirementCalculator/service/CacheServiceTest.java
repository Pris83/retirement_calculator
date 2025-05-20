package com.example.retirementCalculator.service;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import com.example.retirementCalculator.exception.CacheUpdateException;
import com.example.retirementCalculator.exception.RedisCacheAccessException;
import com.example.retirementCalculator.exception.RedisCacheDeleteException;
import com.example.retirementCalculator.exception.RedisCacheUpdateException;
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
import java.util.Set;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CacheServiceTest {

    private static final String KEY_SIMPLE = "simple";
    private static final String KEY_UNKNOWN = "unknown";
    private static final String KEY_FANCY = "fancy";
    private static final String CACHE_NAME = "cacheName";
    private List<LifestyleDeposit> deposits;

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

    @Mock
    private RedisConnectionFactory connectionFactory;

    @Mock
    private RedisConnection connection;

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(connection);
    }

    @Test
    void testGetCacheStatus_RedisCache() {
        when(connection.ping()).thenReturn("PONG");
        when(redisTemplate.opsForValue().size(CACHE_NAME)).thenReturn(10L);
        when(valueOperations.get(CACHE_NAME)).thenReturn(null); // simulate key not present

        String status = cacheService.getCacheStatus(CACHE_NAME);

        assertEquals("Redis is UP | Cache key cacheName does NOT exist | Approximate size: 10", status);
    }

    @Test
    void testGetCacheStatus_CacheManagerCache() {
        when(redisTemplate.getConnectionFactory()).thenReturn(null);
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);

        String status = cacheService.getCacheStatus(CACHE_NAME);

        assertEquals("Cache is initialized (non-Redis), but size is unknown.", status);
    }

    @Test
    void testGetCacheStatus_NoCache() {
        when(redisTemplate.getConnectionFactory()).thenReturn(null);
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(null);

        String status = cacheService.getCacheStatus(CACHE_NAME);

        assertEquals("Cache is NOT initialized (no Redis connection or fallback cache).", status);
    }

    @Test
    void testGetCacheStatus_ThrowsRedisConnectionFailureException() {
        when(connection.ping()).thenThrow(new RuntimeException("Redis connection failure"));

        String status = cacheService.getCacheStatus(CACHE_NAME);

        assertTrue(status.contains("Redis connection failure"));
    }

    @Test
    void testGetCacheStatus_ThrowsCacheInitializationException() {
        when(redisTemplate.getConnectionFactory()).thenReturn(null);
        when(cacheManager.getCache(CACHE_NAME)).thenThrow(new RuntimeException("Cache initialization failed"));

        String status = cacheService.getCacheStatus(CACHE_NAME);

        assertTrue(status.contains("Cache initialization failed"));
    }

    @Test
    void testRefreshCache_Found() {
        LifestyleDeposit deposit = new LifestyleDeposit();
        deposit.setLifestyleType(KEY_SIMPLE);
        deposit.setMonthlyDeposit(BigDecimal.valueOf(1000.0));

        when(retirementRepository.findByLifestyleType(KEY_SIMPLE)).thenReturn(Optional.of(deposit));

        String expectedValue = "LifestyleType: simple, Amount: 1000.0";

        String result = cacheService.refreshCache(KEY_SIMPLE);

        verify(redisTemplate).delete(KEY_SIMPLE);
        verify(valueOperations).set(KEY_SIMPLE, expectedValue);
        assertEquals("Cache refreshed for key: simple with value: " + expectedValue, result);
    }

    @Test
    void testRefreshCache_NotFound() {
        when(retirementRepository.findByLifestyleType(KEY_UNKNOWN)).thenReturn(Optional.empty());

        String result = cacheService.refreshCache(KEY_UNKNOWN);

        assertTrue(result.contains("Error refreshing cache for key: " + KEY_UNKNOWN));
        verify(redisTemplate).delete(KEY_UNKNOWN);
        verify(valueOperations, never()).set(anyString(), anyString());
    }

    @Test
    void testRefreshCache_ThrowsCacheUpdateException() {
        String key = "key";
        doThrow(new RuntimeException("Redis down")).when(redisTemplate).delete(key);

        String result = cacheService.refreshCache(key);

        assertTrue(result.contains("Error refreshing cache for key: " + key + " - Redis down"));
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
    void testFetchFromCache_ThrowsRedisCacheAccessException() {
        when(valueOperations.get("key")).thenThrow(new RedisCacheAccessException("Redis connection error"));

        assertThatThrownBy(() -> cacheService.fetchFromCache("key"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error fetching data from cache");
    }

    @Test
    void testUpdateCache_Success() {
        doNothing().when(valueOperations).set("key", "newValue");

        cacheService.updateCache("key", "newValue");

        verify(valueOperations).set("key", "newValue");
    }

    @Test
    void testUpdateCache_ThrowsRedisCacheUpdateException() {
        doThrow(new RedisCacheUpdateException("Redis update error")).when(valueOperations).set("key", "value");

        assertThatThrownBy(() -> cacheService.updateCache("key", "value"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cache update failed");
    }

    @Test
    void testDeleteFromCache_Success() {
        cacheService.deleteFromCache("key");

        verify(redisTemplate).delete("key");
    }

    @Test
    void testDeleteFromCache_ThrowsRedisCacheDeleteException() {
        doThrow(new RedisCacheDeleteException("Redis delete error")).when(redisTemplate).delete("key");

        assertThatThrownBy(() -> cacheService.deleteFromCache("key"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error deleting cache for key");
    }

    @Test
    void testRefreshAllCache_Success() {
        when(redisTemplate.keys("*")).thenReturn(Set.of(KEY_SIMPLE, KEY_FANCY));

        deposits = List.of(
                new LifestyleDeposit(KEY_SIMPLE, new BigDecimal("1000")),
                new LifestyleDeposit(KEY_FANCY, new BigDecimal("3000"))
        );
        when(retirementRepository.findAll()).thenReturn(deposits);

        // Act
        String response = cacheService.refreshAllCache();

        // Assert
        assertEquals("Cache successfully refreshed for all LifestyleDeposit entries.", response);

        verify(redisTemplate).delete(Set.of(KEY_SIMPLE, KEY_FANCY));
        verify(redisTemplate.opsForValue(), times(2)).set(anyString(), anyString());
    }



    @Test
    void testRefreshAllCache_NotFound() {
        // Mock Redis keys present
        when(redisTemplate.keys("*")).thenReturn(Set.of(KEY_SIMPLE, KEY_FANCY));

        // Mock DB returns empty list (no deposits)
        when(retirementRepository.findAll()).thenReturn(List.of());

        // Act
        String response = cacheService.refreshAllCache();

        // Assert
        assertEquals("No LifestyleDeposit records found in the database.", response);

        verify(redisTemplate).delete(Set.of(KEY_SIMPLE, KEY_FANCY));
        verify(redisTemplate.opsForValue(), never()).set(anyString(), anyString());
    }

    @Test
    void testRefreshAllCache_Exception() {
        // Simulate Redis keys fetch returns some keys
        when(redisTemplate.keys("*")).thenReturn(Set.of(KEY_SIMPLE));

        // Simulate delete throws a runtime exception (e.g. Redis down)
        doThrow(new RuntimeException("Redis down")).when(redisTemplate).delete(Set.of(KEY_SIMPLE));

        // Act
        String response = cacheService.refreshAllCache();

        // Assert
        assertTrue(response.contains("Error refreshing all cache entries: Redis down"));

        verify(redisTemplate).delete(Set.of(KEY_SIMPLE));
        verify(redisTemplate.opsForValue(), never()).set(anyString(), anyString());
    }
}
