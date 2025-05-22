package com.example.retirementCalculator.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Configuration class for setting up two Redis databases using Spring Data Redis.
 * One database (DB 0) is used for caching monthly deposit values,
 * and another database (DB 1) is used for storing interest rate values.
 */
@Configuration
public class RedisConfig {

    /**
     * Creates a Redis connection factory for database 0.
     *
     * @return the configured LettuceConnectionFactory for DB 0
     */
    @Bean
    @Primary
    public LettuceConnectionFactory redisConnectionFactoryDb0() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        config.setDatabase(0); // Logical Redis DB 0
        return new LettuceConnectionFactory(config);
    }

    /**
     * Creates a Redis connection factory for database 1.
     *
     * @return the configured LettuceConnectionFactory for DB 1
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactoryDb1() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        config.setDatabase(1); // Logical Redis DB 1
        return new LettuceConnectionFactory(config);
    }

    /**
     * Configures a StringRedisTemplate for interacting with Redis DB 0.
     * Used for storing and retrieving monthly deposit values.
     */
    @Bean(name = "stringRedisTemplateDb0")
    public StringRedisTemplate stringRedisTemplateDb0(@Qualifier("redisConnectionFactoryDb0") LettuceConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    /**
     * Configures a StringRedisTemplate for interacting with Redis DB 1.
     * Used for storing and retrieving interest rate values.
     */
    @Bean(name = "stringRedisTemplateDb1")
    public StringRedisTemplate stringRedisTemplateDb1(@Qualifier("redisConnectionFactoryDb1") LettuceConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    /**
     * Configures a CacheManager backed by Redis DB 0.
     * Sets a default TTL of 30 minutes and uses JSON serialization for cache values.
     */
    @Bean
    public CacheManager cacheManager(@Qualifier("redisConnectionFactoryDb0") LettuceConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // Default TTL for cache entries
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(factory).cacheDefaults(config).build();
    }
}
