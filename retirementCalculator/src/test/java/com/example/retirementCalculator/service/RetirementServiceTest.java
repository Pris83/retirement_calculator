package com.example.retirementCalculator.service;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import com.example.retirementCalculator.entity.Retirement;
import com.example.retirementCalculator.entity.RetirementResult;
import com.example.retirementCalculator.exception.CalculationException;
import com.example.retirementCalculator.exception.InvalidInputException;
import com.example.retirementCalculator.exception.LifestyleNotFoundException;
import com.example.retirementCalculator.repository.RetirementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RetirementServiceTest {

    @Autowired
    private RetirementService service;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RetirementRepository repository;

    private static final String TEST_LIFESTYLE_TYPE = "fancy";
    private static final String TEST_DEPOSIT_VALUE = "3000";

    @BeforeEach
    void setUp() {
        // Clear DB and Redis for clean state
        repository.deleteAll();
        redisTemplate.delete(TEST_LIFESTYLE_TYPE);

        // Save lifestyle deposit in DB
        LifestyleDeposit deposit = new LifestyleDeposit();
        deposit.setLifestyleType(TEST_LIFESTYLE_TYPE);
        deposit.setMonthlyDeposit(new BigDecimal(TEST_DEPOSIT_VALUE));
        repository.save(deposit);

        // Save in Redis
        redisTemplate.opsForValue().set(TEST_LIFESTYLE_TYPE, TEST_DEPOSIT_VALUE);
    }


    @Test
    void shouldCalculateRetirementUsingH2Data() {
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType("fancy");

        RetirementResult result = service.calculatePlan(input);

        assertThat(result).isNotNull();
        assertThat(result.getMonthlyDeposit()).isEqualTo(BigDecimal.valueOf(3000));
    }

    @Test
    void shouldThrowExceptionWhenCurrentAgeIsNegative() {
        Retirement input = new Retirement();
        input.setCurrentAge(-1);
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType(TEST_LIFESTYLE_TYPE);

        assertThrows(InvalidInputException.class, () -> service.calculatePlan(input));
    }

    @Test
    void shouldThrowExceptionWhenRetirementAgeNotGreaterThanCurrentAge() {
        Retirement input = new Retirement();
        input.setCurrentAge(65);
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType(TEST_LIFESTYLE_TYPE);

        assertThrows(InvalidInputException.class, () -> service.calculatePlan(input));
    }

    @Test
    void shouldThrowExceptionWhenInterestRateIsNegative() {
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(-1.0);
        input.setLifestyleType(TEST_LIFESTYLE_TYPE);

        assertThrows(InvalidInputException.class, () -> service.calculatePlan(input));
    }

    @Test
    void shouldThrowLifestyleNotFoundExceptionWhenDepositMissingInRedis() {
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType("unknown");

        assertThrows(LifestyleNotFoundException.class, () -> service.calculatePlan(input));
    }

    @Test
    void shouldCalculateCorrectlyWhenInterestRateIsZero() {
        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(0.0);
        input.setLifestyleType(TEST_LIFESTYLE_TYPE);

        RetirementResult result = service.calculatePlan(input);

        int months = (65 - 30) * 12;
        BigDecimal expectedFutureValue = new BigDecimal(TEST_DEPOSIT_VALUE)
                .multiply(BigDecimal.valueOf(months))
                .setScale(2, RoundingMode.HALF_UP);

        assertThat(result.getFutureValue()).isEqualByComparingTo(expectedFutureValue);
    }

    @Test
    void shouldHandleUnexpectedExceptionDuringCalculation() {
        // You can use a Mockito spy or mock to simulate Redis throwing an exception
        // Here is a simplified example using Mockito:

        var redisMock = org.mockito.Mockito.mock(StringRedisTemplate.class);
        var opsMock = org.mockito.Mockito.mock(org.springframework.data.redis.core.ValueOperations.class);
        org.mockito.Mockito.when(redisMock.opsForValue()).thenReturn(opsMock);
        org.mockito.Mockito.when(opsMock.get(org.mockito.Mockito.anyString()))
                .thenThrow(new RuntimeException("Redis down"));

        RetirementService serviceWithMock = new RetirementService(redisMock, repository);

        Retirement input = new Retirement();
        input.setCurrentAge(30);
        input.setRetirementAge(65);
        input.setInterestRate(5.0);
        input.setLifestyleType(TEST_LIFESTYLE_TYPE);

        assertThrows(CalculationException.class, () -> serviceWithMock.calculatePlan(input));
    }
}
