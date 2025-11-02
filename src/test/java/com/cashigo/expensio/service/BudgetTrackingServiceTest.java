package com.cashigo.expensio.service;

import com.cashigo.expensio.model.*;
import com.cashigo.expensio.repository.TransactionRepository;
import com.cashigo.expensio.service.budget.BudgetTrackingService;
import com.cashigo.expensio.service.category.SubCategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BudgetTrackingServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private SubCategoryService subCategoryService;

    @InjectMocks
    private BudgetTrackingService budgetTrackingService;

    @Test
    void whenFetchingTransactionsOfUserBasedOnCycle_thenCycleIsSetOnTransactions() {
        BudgetCycle budgetCycle = new BudgetCycle();
        BudgetDefinition budgetDefinition = new BudgetDefinition();
        String userId = UUID.randomUUID().toString();
        budgetDefinition.setUserId(userId);
        Category category = new Category();
        category.setId(3L);
        budgetDefinition.setCategory(category);
        budgetCycle.setBudgetDefinition(budgetDefinition);
        budgetCycle.setCycleStartDateTime(Instant.now().minusSeconds(60 * 60));
        budgetCycle.setCycleEndDateTime(Instant.now().plusSeconds(60 * 60));
        List<SubCategory> subCategories = List.of(createSubCategory(), createSubCategory());
        List<Transaction> transactions = List.of(new Transaction(), new Transaction(), new Transaction());

        when(subCategoryService.getSubCategoryEntities(anyLong())).thenReturn(subCategories);
        when(transactionRepository
                .findTransactionsByInstantRangeWithSubCategories(eq(userId), anyList(), any(Instant.class), any(Instant.class)))
                .thenReturn(transactions);

        List<Transaction> actualResult = budgetTrackingService.getTransactionsOfCurrentBudgetCycle(budgetCycle);

        assertThat(actualResult).allSatisfy(transaction -> {
            assertThat(transaction).extracting(Transaction::getBudgetCycle).isEqualTo(budgetCycle);
        });
    }

    private SubCategory createSubCategory() {
        SubCategory subCategory = new SubCategory();
        subCategory.setId(new Random().nextLong(50) + 40);
        return subCategory;
    }

}
