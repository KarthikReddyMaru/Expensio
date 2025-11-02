package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.BudgetDefinitionDto;
import com.cashigo.expensio.dto.exception.NoBudgetDefinitionFoundException;
import com.cashigo.expensio.dto.exception.NoCategoryFoundException;
import com.cashigo.expensio.dto.mapper.BudgetDefinitionMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
import com.cashigo.expensio.repository.CategoryRepository;
import com.cashigo.expensio.repository.TransactionRepository;
import com.cashigo.expensio.service.budget.BudgetCycleService;
import com.cashigo.expensio.service.budget.BudgetDefinitionService;
import com.cashigo.expensio.service.budget.BudgetTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BudgetDefinitionServiceTest {

    @Mock
    private BudgetDefinitionRepository budgetDefinitionRepository;
    @Mock
    private BudgetCycleService budgetCycleService;
    @Mock
    private BudgetDefinitionMapper budgetDefinitionMapper;
    @Mock
    private BudgetTrackingService budgetTrackingService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BudgetDefinitionService budgetDefinitionService;

    @BeforeEach
    public void init() {
        String currentLoggedInUserId = UUID.randomUUID().toString();
        try(MockedStatic<UserContext> mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getUserId).thenReturn(currentLoggedInUserId);
        }
    }


    @Test
    void whenSavingBudgetDefinition_thenItIsPersisted() {
        Long categoryId = 2L;
        BudgetDefinitionDto budgetDefinitionDto = createBudgetDefinitionDto(categoryId);
        BudgetDefinition unsavedBudgetDefinition = createBudgetDefinition(categoryId);
        BudgetDefinition savedBudgetDefinition = new BudgetDefinition();
        BudgetDefinitionDto savedBudgetDefinitionDto = new BudgetDefinitionDto();
        List<Transaction> transactions = List.of(new Transaction(), new Transaction(), new Transaction());
        BudgetCycle budgetCycle = new BudgetCycle();

        when(budgetDefinitionMapper.mapToEntity(budgetDefinitionDto)).thenReturn(unsavedBudgetDefinition);
        when(categoryRepository.existsCategoryById(categoryId, UserContext.getUserId())).thenReturn(true);
        when(budgetCycleService.createBudgetCycle(unsavedBudgetDefinition)).thenReturn(budgetCycle);
        when(budgetDefinitionRepository.save(unsavedBudgetDefinition)).thenReturn(savedBudgetDefinition);
        when(budgetTrackingService.getTransactionsOfCurrentBudgetCycle(budgetCycle)).thenReturn(transactions);
        when(budgetDefinitionMapper.mapToDto(any(BudgetDefinition.class))).thenReturn(savedBudgetDefinitionDto);

        BudgetDefinitionDto actualDto = budgetDefinitionService.saveBudgetDefinition(budgetDefinitionDto);

        InOrder inOrder = inOrder(categoryRepository, budgetCycleService,
                budgetDefinitionRepository, budgetTrackingService, transactionRepository);
        inOrder.verify(categoryRepository).existsCategoryById(categoryId, UserContext.getUserId());
        inOrder.verify(budgetCycleService).createBudgetCycle(unsavedBudgetDefinition);
        inOrder.verify(budgetDefinitionRepository).save(unsavedBudgetDefinition);
        inOrder.verify(budgetTrackingService).getTransactionsOfCurrentBudgetCycle(budgetCycle);
        inOrder.verify(transactionRepository).saveAll(transactions);

        assertThat(actualDto).isEqualTo(savedBudgetDefinitionDto);
    }

    @Test
    void whenSavingBudgetDefinitionWithInvalidCategory_thenExceptionIsThrown() {
        Long categoryId = 2L;
        BudgetDefinitionDto budgetDefinitionDto = createBudgetDefinitionDto(categoryId);
        BudgetDefinition unsavedBudgetDefinition = createBudgetDefinition(categoryId);

        when(budgetDefinitionMapper.mapToEntity(budgetDefinitionDto)).thenReturn(unsavedBudgetDefinition);
        when(categoryRepository.existsCategoryById(categoryId, UserContext.getUserId())).thenReturn(false);

        assertThatThrownBy(() ->
                budgetDefinitionService.saveBudgetDefinition(budgetDefinitionDto)
        ).isInstanceOf(NoCategoryFoundException.class);

        InOrder inOrder = inOrder(categoryRepository, budgetCycleService,
                budgetDefinitionRepository, budgetTrackingService, transactionRepository);
        inOrder.verify(categoryRepository).existsCategoryById(categoryId, UserContext.getUserId());
        inOrder.verify(budgetCycleService, never()).createBudgetCycle(unsavedBudgetDefinition);
        inOrder.verify(budgetDefinitionRepository, never()).save(unsavedBudgetDefinition);
        inOrder.verify(budgetTrackingService, never()).getTransactionsOfCurrentBudgetCycle(any(BudgetCycle.class));
        inOrder.verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void whenUpdatingBudgetDefinition_thenOnlyAmountIsUpdated() {
        Long categoryId = 3L;
        BigDecimal updatedAmount = BigDecimal.valueOf(2999L);
        UUID budgetDefinitionId = UUID.randomUUID();
        BudgetDefinitionDto budgetDefinitionDto = createBudgetDefinitionDto(categoryId);
        budgetDefinitionDto.setBudgetAmount(updatedAmount);
        budgetDefinitionDto.setId(budgetDefinitionId);
        BudgetDefinition fetchedBudgetDefinition = createBudgetDefinition(categoryId);
        BudgetDefinition savedBudgetDefinition = new BudgetDefinition();
        BudgetDefinitionDto savedBudgetDefinitionDto = new BudgetDefinitionDto();

        when(budgetDefinitionRepository.findBudgetDefinitionByIdAndUserId(budgetDefinitionId, UserContext.getUserId()))
                .thenReturn(Optional.of(fetchedBudgetDefinition));
        when(budgetDefinitionRepository.save(fetchedBudgetDefinition)).thenReturn(savedBudgetDefinition);
        when(budgetDefinitionMapper.mapToDto(savedBudgetDefinition)).thenReturn(savedBudgetDefinitionDto);

        budgetDefinitionService.updateBudgetDefinition(budgetDefinitionDto);

        verify(budgetDefinitionRepository).save(
                argThat(definition ->
                        definition.getBudgetAmount().equals(updatedAmount) &&
                        definition.getUserId().equals(UserContext.getUserId())
                )
        );
        verify(budgetDefinitionMapper).mapToDto(savedBudgetDefinition);
    }

    @Test
    void whenUpdatingOtherUserBudgetDefinition_thenExceptionIsThrown() {
        Long categoryId = 3L;
        BigDecimal updatedAmount = BigDecimal.valueOf(2999L);
        UUID budgetDefinitionId = UUID.randomUUID();
        BudgetDefinitionDto budgetDefinitionDto = createBudgetDefinitionDto(categoryId);
        budgetDefinitionDto.setBudgetAmount(updatedAmount);
        budgetDefinitionDto.setId(budgetDefinitionId);

        when(budgetDefinitionRepository.findBudgetDefinitionByIdAndUserId(budgetDefinitionId, UserContext.getUserId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> budgetDefinitionService.updateBudgetDefinition(budgetDefinitionDto))
                .isInstanceOf(NoBudgetDefinitionFoundException.class);

        verify(budgetDefinitionRepository, never()).save(any(BudgetDefinition.class));
        verify(budgetDefinitionMapper, never()).mapToDto(any(BudgetDefinition.class));
    }

    private BudgetDefinition createBudgetDefinition(Long categoryId) {
        BudgetDefinition unsavedBudgetDefinition = new BudgetDefinition();
        Category category = createCategory(categoryId);
        unsavedBudgetDefinition.setCategory(category);
        return unsavedBudgetDefinition;
    }

    private BudgetDefinitionDto createBudgetDefinitionDto(Long categoryId) {
        BudgetDefinitionDto budgetDefinitionDto = new BudgetDefinitionDto();
        budgetDefinitionDto.setCategoryId(categoryId);
        return budgetDefinitionDto;
    }

    private Category createCategory(Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

}
