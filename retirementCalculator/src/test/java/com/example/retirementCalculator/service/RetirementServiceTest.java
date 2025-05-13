package com.example.retirementCalculator.service;
import com.example.retirementCalculator.entity.Retirement;
import com.example.retirementCalculator.entity.RetirementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RetirementServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;
    private RetirementService service;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        service = new RetirementService(redisTemplate);
        // Use reflection or constructor if needed
        setRedisTemplateManually(service, redisTemplate);
    }

    private void setRedisTemplateManually(RetirementService service, StringRedisTemplate redisTemplate) {
        // Use reflection to inject if field is private
        try {
            var field = RetirementService.class.getDeclaredField("redisTemplate");
            field.setAccessible(true);
            field.set(service, redisTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCalculatePlan_success() {
        // Given
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType("fancy");

        when(valueOps.get("fancy")).thenReturn("3000");

        // When
        RetirementResult result = service.calculatePlan(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCurrentAge()).isEqualTo(30);
        assertThat(result.getMonthlyDeposit()).isEqualTo(3000.0);
        assertThat(result.getFutureValue()).isGreaterThan(0);
    }

    @Test
    void testCalculatePlan_missingLifestyleInRedis() {
        // Given
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType("unknown");

        when(valueOps.get("unknown")).thenReturn(null);

        // Then
        assertThatThrownBy(() -> service.calculatePlan(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No deposit amount configured for lifestyle type: unknown");
    }

    @Test
    void testCalculatePlan_zeroInterestRate() {
        // Given
        Retirement input = new Retirement();
        input.setCurrentAge(25);
        input.setRetirementAge(35);
        input.setInterestRate(0.0);
        input.setLifestyleType("simple");

        when(valueOps.get("simple")).thenReturn("1000");

        // When
        RetirementResult result = service.calculatePlan(input);

        // Then
        assertThat(result.getFutureValue()).isEqualTo(1000.0 * 12 * 10);
    }
}
