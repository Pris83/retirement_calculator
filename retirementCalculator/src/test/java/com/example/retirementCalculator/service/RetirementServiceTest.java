package com.example.retirementCalculator.service;

import com.example.retirementCalculator.entity.Retirement;
import com.example.retirementCalculator.entity.RetirementResult;
import com.example.retirementCalculator.exception.CalculationException;
import com.example.retirementCalculator.exception.InvalidInputException;
import com.example.retirementCalculator.exception.LifestyleNotFoundException;
import com.example.retirementCalculator.repository.RetirementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RetirementServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;
    private RetirementService service;
    private RetirementRepository repository;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        repository = mock(RetirementRepository.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        service = new RetirementService(redisTemplate,repository);
    }

    @Test
    @DisplayName("Should successfully calculate retirement plan with valid input")
    void shouldCalculatePlanForValidInputSuccessfully() {
        // Given
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType("fancy");

        when(valueOps.get("fancy")).thenReturn("3000.0");

        // When
        RetirementResult result = service.calculatePlan(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCurrentAge()).isEqualTo(30);
        assertThat(result.getMonthlyDeposit()).isEqualTo(BigDecimal.valueOf(3000.0));
        assertThat(result.getFutureValue()).isGreaterThan(BigDecimal.valueOf(0.0));
    }

    @Test
    @DisplayName("Should throw LifestyleNotFoundException when lifestyle type is missing in Redis")
    void shouldThrowLifestyleNotFoundExceptionWhenLifestyleIsMissing() {
        // Given
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType("unknown");

        when(valueOps.get("unknown")).thenReturn(null);

        // Then
        assertThatThrownBy(() -> service.calculatePlan(input))
                .isInstanceOf(LifestyleNotFoundException.class)
                .hasMessageContaining("No deposit amount configured for lifestyle type: unknown");
    }

    @Test
    @DisplayName("Should calculate future value correctly when interest rate is zero")
    void shouldCalculateCorrectFutureValueForZeroInterestRate() {
        // Given
        Retirement input = new Retirement();
        input.setCurrentAge(25);
        input.setRetirementAge(35);
        input.setInterestRate(0.0);
        input.setLifestyleType("simple");

        when(valueOps.get("simple")).thenReturn("1000.0");

        // When
        RetirementResult result = service.calculatePlan(input);

        // Then
        BigDecimal expectedFutureValue = BigDecimal.valueOf(1000.0 * 12 * 10).setScale(1, RoundingMode.HALF_UP);
        assertThat(result.getFutureValue().setScale(1, RoundingMode.HALF_UP)).isEqualTo(expectedFutureValue);
    }

    @Test
    @DisplayName("Should throw InvalidInputException when current age is negative")
    void shouldThrowInvalidInputExceptionForNegativeCurrentAge() {
        // Given
        Retirement input = new Retirement();
        input.setCurrentAge(-1); // Invalid current age
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType("fancy");

        // Then
        assertThatThrownBy(() -> service.calculatePlan(input))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Invalid input: currentAge - must be non-negative");
    }

    @Test
    @DisplayName("Should throw InvalidInputException when retirement age is less than current age")
    void shouldThrowInvalidInputExceptionForRetirementAgeLessThanCurrentAge() {
        // Given
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(25); // Invalid retirement age
        input.setInterestRate(5.0);
        input.setLifestyleType("fancy");

        // Then
        assertThatThrownBy(() -> service.calculatePlan(input))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Invalid input: retirementAge - must be greater than currentAge");
    }

    @Test
    @DisplayName("Should throw InvalidInputException when interest rate is negative")
    void shouldThrowInvalidInputExceptionForNegativeInterestRate() {
        // Given
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(-5.0); // Invalid interest rate
        input.setLifestyleType("fancy");

        // Then
        assertThatThrownBy(() -> service.calculatePlan(input))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Invalid input: interestRate - must be non-negative");
    }

    @Test
    @DisplayName("Should throw CalculationException when an unexpected error occurs")
    void shouldThrowCalculationExceptionForUnexpectedError() {
        // Given
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType("fancy");

        // Simulate an unexpected error
        when(valueOps.get("fancy")).thenThrow(new RuntimeException("Unexpected error"));

        // Then
        assertThatThrownBy(() -> service.calculatePlan(input))
                .isInstanceOf(CalculationException.class)
                .hasMessageContaining("Unexpected error during retirement calculation");
    }
}
