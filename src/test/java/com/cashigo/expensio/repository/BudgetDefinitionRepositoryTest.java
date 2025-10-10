package com.cashigo.expensio.repository;

import com.cashigo.expensio.common.consts.BudgetRecurrence;
import com.cashigo.expensio.config.RepositoryConfig;
import com.cashigo.expensio.dto.exception.NoBudgetCycleFoundException;
import com.cashigo.expensio.dto.exception.NoBudgetDefinitionFoundException;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
@Import(RepositoryConfig.class)
public class BudgetDefinitionRepositoryTest {

    @Autowired
    private BudgetDefinitionRepository budgetDefinitionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String currentLoggedInUserId;

    @BeforeEach
    void init() {
        currentLoggedInUserId = UUID.randomUUID().toString();
    }

    @Test
    void whenFetchingBudgetDefinitionById_thenItIsReturnedSuccessfully() {
        BigDecimal amount = BigDecimal.valueOf(1000L);
        BudgetDefinition budgetDefinition = createBudgetDefinition(currentLoggedInUserId, amount);
        BudgetDefinition fetchBudgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(budgetDefinition.getId(), currentLoggedInUserId)
                .orElseThrow();

        assertThat(fetchBudgetDefinition)
                .extracting(BudgetDefinition::getBudgetAmount, BudgetDefinition::getUserId)
                .containsExactly(amount, currentLoggedInUserId);
        assertThat(fetchBudgetDefinition.getCategory()).isNotNull();
    }

    @Test
    void whenFetchingOtherUserBudgetDefinitionById_thenExceptionIsThrown() {
        String otherUserId = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.valueOf(1000L);
        BudgetDefinition budgetDefinition = createBudgetDefinition(otherUserId, amount);

        assertThatThrownBy(() ->
                budgetDefinitionRepository
                        .findBudgetDefinitionByIdAndUserId(budgetDefinition.getId(), currentLoggedInUserId)
                        .orElseThrow(NoBudgetDefinitionFoundException::new)
        ).isInstanceOf(NoBudgetDefinitionFoundException.class);
    }

    @Test
    void whenFetchingBudgetDefinitionByCategoryId_thenItIsReturnedSuccessfully() {
        BigDecimal amount = BigDecimal.valueOf(1000L);
        BudgetDefinition budgetDefinition = createBudgetDefinition(currentLoggedInUserId, amount);
        BudgetDefinition fetchBudgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByCategoryWithCycles(budgetDefinition.getCategory().getId(), currentLoggedInUserId)
                .orElseThrow();

        assertThat(fetchBudgetDefinition)
                .extracting(BudgetDefinition::getBudgetAmount, BudgetDefinition::getUserId)
                .containsExactly(amount, currentLoggedInUserId);
        assertThat(fetchBudgetDefinition.getCategory()).isNotNull();
    }

    @Test
    void whenFetchingOtherUserBudgetDefinitionByCategoryId_thenExceptionIsThrown() {
        String otherUserId = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.valueOf(1000L);
        BudgetDefinition budgetDefinition = createBudgetDefinition(otherUserId, amount);
        BudgetDefinition fetchBudgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByCategoryWithCycles(budgetDefinition.getCategory().getId(), otherUserId)
                .orElseThrow();

        assertThat(fetchBudgetDefinition.getId()).isNotNull();

        assertThatExceptionOfType(NoBudgetDefinitionFoundException.class).isThrownBy(() ->
                budgetDefinitionRepository
                        .findBudgetDefinitionByCategoryWithCycles(budgetDefinition.getCategory().getId(), currentLoggedInUserId)
                        .orElseThrow(NoBudgetDefinitionFoundException::new)
        );
    }

    @Test
    void whenUpdatingBudgetDefinition_thenItIsUpdated() {
        BigDecimal amount = BigDecimal.valueOf(1000L);
        BudgetDefinition budgetDefinition = createBudgetDefinition(currentLoggedInUserId, amount);
        BudgetDefinition fetchBudgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(budgetDefinition.getId(), currentLoggedInUserId)
                .orElseThrow();

        assertThat(fetchBudgetDefinition.getId()).isNotNull();

        BigDecimal updatedAmount = BigDecimal.valueOf(1499L);
        fetchBudgetDefinition.setBudgetAmount(updatedAmount);
        budgetDefinitionRepository.save(fetchBudgetDefinition);

        fetchBudgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(fetchBudgetDefinition.getId(), currentLoggedInUserId)
                .orElseThrow();

        assertThat(fetchBudgetDefinition)
                .extracting(BudgetDefinition::getBudgetAmount, BudgetDefinition::getUserId)
                .containsExactly(updatedAmount, currentLoggedInUserId);
        assertThat(fetchBudgetDefinition.getCategory()).isNotNull();
    }

    @Test
    void whenDeletingBudgetDefinition_thenItIsRemovedFromDB() {
        BigDecimal amount = BigDecimal.valueOf(1000L);
        BudgetDefinition budgetDefinition = createBudgetDefinition(currentLoggedInUserId, amount);
        BudgetDefinition fetchBudgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(budgetDefinition.getId(), currentLoggedInUserId)
                .orElseThrow();

        assertThat(fetchBudgetDefinition.getId()).isNotNull();

        budgetDefinitionRepository
                .deleteBudgetDefinitionByIdAndUserId(fetchBudgetDefinition.getId(), currentLoggedInUserId);

        assertThat(budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(budgetDefinition.getId(), currentLoggedInUserId))
                .isEmpty();
    }

    @Test
    void whenDeletingOtherUserBudgetDefinition_thenItIsNotRemovedFromDB() {
        String otherUserId = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.valueOf(1000L);
        BudgetDefinition budgetDefinition = createBudgetDefinition(otherUserId, amount);
        BudgetDefinition fetchBudgetDefinition = budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(budgetDefinition.getId(), otherUserId)
                .orElseThrow();

        assertThat(fetchBudgetDefinition.getId()).isNotNull();

        budgetDefinitionRepository
                .deleteBudgetDefinitionByIdAndUserId(fetchBudgetDefinition.getId(), currentLoggedInUserId);

        assertThat(budgetDefinitionRepository
                .findBudgetDefinitionByIdAndUserId(budgetDefinition.getId(), otherUserId))
                .isPresent();
    }

    BudgetDefinition createBudgetDefinition(String userId, BigDecimal amount) {
        BudgetDefinition budgetDefinition = new BudgetDefinition();
        budgetDefinition.setBudgetAmount(amount);
        budgetDefinition.setCategory(createAndSaveCustomCategory(userId));
        budgetDefinition.setUserId(userId);
        budgetDefinition.setBudgetRecurrenceType(BudgetRecurrence.WEEKLY);
        return budgetDefinitionRepository.saveAndFlush(budgetDefinition);
    }

    Category createAndSaveCustomCategory(String userId) {
        Category category = new Category();
        category.setName("CustomCategory");
        category.setUserId(userId);
        category.setSystem(false);
        return categoryRepository.saveAndFlush(category);
    }
}
