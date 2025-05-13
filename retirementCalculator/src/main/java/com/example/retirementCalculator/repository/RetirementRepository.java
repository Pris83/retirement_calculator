package com.example.retirementCalculator.repository;

import com.example.retirementCalculator.entity.LifestyleDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing {@link LifestyleDeposit} data from the database.
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations and custom queries
 * related to different lifestyle types.
 * </p>
 *
 * <p>
 * Example methods inherited from JpaRepository:
 * <ul>
 *     <li>{@code save(LifestyleDeposit entity)}</li>
 *     <li>{@code findById(Long id)}</li>
 *     <li>{@code deleteById(Long id)}</li>
 *     <li>{@code findAll()}</li>
 * </ul>
 * </p>
 *
 * <p>
 * Custom query methods:
 * <ul>
 *     <li>{@code Optional<LifestyleDeposit> findByLifestyleType(String lifestyleType)} — finds the deposit configuration for a specific lifestyle type (e.g., "simple" or "fancy").</li>
 * </ul>
 * </p>
 *
 * @author Priscilla Masunyane
 */
@Repository
public interface RetirementRepository extends JpaRepository<LifestyleDeposit, Long> {

    /**
     * Finds a {@link LifestyleDeposit} entry by its lifestyle type.
     *
     * @param lifestyleType the type of lifestyle (e.g., "simple", "fancy")
     * @return an {@link Optional} containing the matching {@link LifestyleDeposit}, if found
     */
    Optional<LifestyleDeposit> findByLifestyleType(String lifestyleType);
}

