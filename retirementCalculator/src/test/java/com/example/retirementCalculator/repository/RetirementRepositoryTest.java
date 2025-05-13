package com.example.retirementCalculator.repository;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RetirementRepositoryTest {


    private final RetirementRepository repository;

    @Autowired
    public RetirementRepositoryTest(RetirementRepository repository) {
        this.repository = repository;
    }

    @Test
    void testFindByLifestyleType_found() {
        // Arrange
        LifestyleDeposit deposit = new LifestyleDeposit();
        deposit.setLifestyleType("simple");
        deposit.setMonthlyDeposit(1000.0);

        repository.save(deposit);

        // Act
        Optional<LifestyleDeposit> result = repository.findByLifestyleType("simple");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getMonthlyDeposit()).isEqualTo(1000.0);
    }

    @Test
    void testFindByLifestyleType_notFound() {
        Optional<LifestyleDeposit> result = repository.findByLifestyleType("nonexistent");
        assertThat(result).isNotPresent();
    }



}
