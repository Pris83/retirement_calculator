package com.example.retirementCalculator.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Configuration class for setting up Redis as the caching and data store mechanism
 * using Spring Data Redis.
 */
@Configuration
public class RedisConfig {

    /**
     * Creates a {@link RedisConnectionFactory} using Lettuce.
     *
     * @return a new instance of {@link LettuceConnectionFactory}
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    /**
     * Configures a {@link RedisTemplate} for performing Redis operations.
     * Uses Jackson JSON serializer for serializing values.
     *
     * @param factory the Redis connection factory
     * @return a configured {@link RedisTemplate} instance
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Use Jackson JSON serializer for value serialization
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setDefaultSerializer(serializer);

        return template;
    }

    /**
     * Configures a {@link CacheManager} for managing caches backed by Redis.
     * Sets default cache TTL to 30 minutes and uses JSON serialization.
     *
     * @param factory the Redis connection factory
     * @return a configured {@link CacheManager} instance
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // Set cache TTL to 30 minutes
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(factory).cacheDefaults(config).build();
    }
}
