package com.example.retirementCalculator.repository;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class RetirementRepositoryTest {

    @Autowired
    private RetirementRepository repository;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should save and retrieve LifestyleDeposit by lifestyleType")
    void testFindByLifestyleType_found() {
        // Arrange
        LifestyleDeposit deposit = new LifestyleDeposit();
        deposit.setLifestyleType("simple");
        deposit.setMonthlyDeposit(BigDecimal.valueOf(1000.0));
        repository.save(deposit);

        // Act
        Optional<LifestyleDeposit> result = repository.findByLifestyleType("simple");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getMonthlyDeposit()).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("Should return empty when lifestyleType not found")
    void testFindByLifestyleType_notFound() {
        Optional<LifestyleDeposit> result = repository.findByLifestyleType("nonexistent");
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("Should update LifestyleDeposit successfully")
    void testUpdateMonthlyDeposit() {
        LifestyleDeposit deposit = new LifestyleDeposit();
        deposit.setLifestyleType("fancy");
        deposit.setMonthlyDeposit(BigDecimal.valueOf(3000.0));

        // Save original
        LifestyleDeposit saved = repository.save(deposit);

        // Update value
        saved.setMonthlyDeposit(BigDecimal.valueOf(3500.0));
        repository.save(saved);

        Optional<LifestyleDeposit> updated = repository.findByLifestyleType("fancy");
        assertThat(updated).isPresent();
        assertThat(updated.get().getMonthlyDeposit()).isEqualByComparingTo("3500.00");
    }


    @Test
    @DisplayName("Should delete LifestyleDeposit")
    void testDeleteLifestyleDeposit() {
        LifestyleDeposit deposit = new LifestyleDeposit();
        deposit.setLifestyleType("simple");
        deposit.setMonthlyDeposit(BigDecimal.valueOf(1000.0));
        LifestyleDeposit saved = repository.save(deposit);

        repository.delete(saved);

        Optional<LifestyleDeposit> deleted = repository.findByLifestyleType("simple");
        assertThat(deleted).isNotPresent();


    }




}
