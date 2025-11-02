package com.cashigo.expensio.service;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.TransactionDto;
import com.cashigo.expensio.dto.exception.NoTransactionFoundException;
import com.cashigo.expensio.dto.mapper.TransactionMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.RecurringTransactionDefinition;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import com.cashigo.expensio.service.budget.BudgetCycleService;
import com.cashigo.expensio.service.transaction.RecurringTransactionService;
import com.cashigo.expensio.service.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private BudgetCycleService budgetCycleService;
    @Mock
    private RecurringTransactionService recurringTransactionService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void init() {
        String currentLoggedInUserId = UUID.randomUUID().toString();
        transactionService.setPageSize(10);
        try(MockedStatic<UserContext> userContext = mockStatic(UserContext.class)) {
            userContext.when(UserContext::getUserId).thenReturn(currentLoggedInUserId);
        }
    }

    @Test
    public void whenFetchingTransactionById_thenDtoIsReturned() {
        UUID transactionId = UUID.randomUUID();
        TransactionDto expectedDto = new TransactionDto();

        when(transactionRepository.findTransactionById(transactionId, UserContext.getUserId()))
                .thenReturn(Optional.of(new Transaction()));
        when(transactionMapper.mapToDto(any(Transaction.class))).thenReturn(expectedDto);

        TransactionDto actualDto = transactionService.getTransactionById(transactionId);

        verify(transactionRepository)
                .findTransactionById(transactionId, UserContext.getUserId());
        verify(transactionMapper).mapToDto(any(Transaction.class));
        assertThat(actualDto).as("Dto").isEqualTo(expectedDto);
    }

    @Test
    public void whenFetchingOtherUserTransactionById_thenExceptionIsThrown() {
        UUID transactionId = UUID.randomUUID();

        when(transactionRepository.findTransactionById(transactionId, UserContext.getUserId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionService.getTransactionById(transactionId)
        ).isInstanceOf(NoTransactionFoundException.class);
        verify(transactionRepository).findTransactionById(transactionId, UserContext.getUserId());
        verify(transactionMapper, never()).mapToDto(any(Transaction.class));
    }

    @Test
    public void whenFetchingRecentTransactionsByPageNum_thenListOfDtoAreReturned() {

        List<Transaction> transactions = Stream.generate(Transaction::new).limit(10).toList();
        Page<Transaction> transactionPage = new PageImpl<>(transactions);

        when(transactionRepository.findTransactionsOfUserWithSubCategories(eq(UserContext.getUserId()), any(Pageable.class)))
                .thenReturn(transactionPage);
        when(transactionMapper.mapToDto(any(Transaction.class))).thenReturn(new TransactionDto());

        List<TransactionDto> transactionDto = transactionService.getAllTransactionByUserId(1);

        verify(transactionRepository).findTransactionsOfUserWithSubCategories(eq(UserContext.getUserId()), any(Pageable.class));
        verify(transactionMapper, times(10)).mapToDto(any(Transaction.class));
        assertThat(transactionDto).isNotNull().hasSize(10);
    }

    @Test
    public void whenSavingTransactionWithNoSubCategory_thenItIsPersistedSuccessfully() {

        TransactionDto transactionDto = new TransactionDto();
        Transaction transaction = new Transaction();
        Transaction savedTransaction = new Transaction();
        TransactionDto savedTransactionDto = new TransactionDto();

        when(transactionMapper.mapToEntity(transactionDto)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(savedTransaction);
        when(transactionMapper.mapToDto(savedTransaction)).thenReturn(savedTransactionDto);

        TransactionDto actualResultDto = transactionService.saveTransaction(transactionDto);

        InOrder inOrder = inOrder(transactionMapper, transactionRepository);
        inOrder.verify(transactionMapper).mapToEntity(transactionDto);
        inOrder.verify(transactionRepository).save(transaction);
        inOrder.verify(transactionMapper).mapToDto(savedTransaction);

        verify(budgetCycleService, never()).getBudgetCycleByInstant(anyLong(), any(Instant.class), eq(UserContext.getUserId()));
        verify(recurringTransactionService, never()).createRecurringTransactionDefinition(any(Transaction.class), any(TransactionRecurrence.class));
        assertThat(actualResultDto).isEqualTo(savedTransactionDto);
    }

    @Test
    public void whenSavingTransactionWithSubCategoryWithActiveBudgetCycle_thenBudgetCycleIsSetForTransactionAndPersisted() {
        Long subCategoryId = 3L;
        Instant transactionDateTime = Instant.now();

        TransactionDto transactionDto = new TransactionDto();

        Transaction transaction = createTransactionWithSubCat(subCategoryId, transactionDateTime);

        BudgetCycle budgetCycle = new BudgetCycle();
        Transaction savedTransaction = new Transaction();
        TransactionDto savedTransactionDto = new TransactionDto();

        when(transactionMapper.mapToEntity(transactionDto)).thenReturn(transaction);
        when(budgetCycleService.getBudgetCycleByInstant(subCategoryId, transactionDateTime, UserContext.getUserId()))
                .thenReturn(budgetCycle);
        when(transactionRepository.save(transaction)).thenReturn(savedTransaction);
        when(transactionMapper.mapToDto(savedTransaction)).thenReturn(savedTransactionDto);

        TransactionDto actualResultDto = transactionService.saveTransaction(transactionDto);

        InOrder inOrder = inOrder(transactionMapper, transactionRepository, budgetCycleService);
        inOrder.verify(transactionMapper).mapToEntity(transactionDto);
        inOrder.verify(budgetCycleService).getBudgetCycleByInstant(subCategoryId, transactionDateTime, UserContext.getUserId());
        inOrder.verify(transactionRepository).save(transaction);
        inOrder.verify(transactionMapper).mapToDto(savedTransaction);

        verify(recurringTransactionService, never())
                .createRecurringTransactionDefinition(any(Transaction.class) , any(TransactionRecurrence.class));
        assertThat(actualResultDto).isEqualTo(savedTransactionDto);
    }

    @Test
    public void whenSavingTransactionWithRecurrenceAndNoSubCategory_thenOnlyTransactionDefinitionIsSetAndPersisted() {
        TransactionDto transactionDto = new TransactionDto();
        TransactionRecurrence recurrence = TransactionRecurrence.WEEKLY;
        transactionDto.setTransactionRecurrenceType(recurrence);
        Transaction unsavedTransaction = new Transaction();
        Transaction savedTransaction = new Transaction();
        TransactionDto savedTransactionDto = new TransactionDto();
        RecurringTransactionDefinition transactionDefinition = new RecurringTransactionDefinition();

        when(transactionMapper.mapToEntity(transactionDto)).thenReturn(unsavedTransaction);
        when(transactionMapper.mapToDto(savedTransaction)).thenReturn(savedTransactionDto);
        when(recurringTransactionService.createRecurringTransactionDefinition(unsavedTransaction, recurrence))
                .thenReturn(transactionDefinition);
        when(transactionRepository.save(unsavedTransaction)).thenReturn(savedTransaction);

        TransactionDto actualDto = transactionService.saveTransaction(transactionDto);

        InOrder inOrder = inOrder(transactionRepository, recurringTransactionService);
        inOrder.verify(recurringTransactionService).createRecurringTransactionDefinition(unsavedTransaction, recurrence);
        inOrder.verify(transactionRepository).save(unsavedTransaction);

        verify(budgetCycleService, never()).getBudgetCycleByInstant(anyLong(), any(Instant.class), eq(UserContext.getUserId()));
        assertThat(actualDto).isEqualTo(savedTransactionDto);
    }

    @Test
    public void whenSavingTransactionWithBothRecurrenceAndSubCategoryWithActiveBudgetCycle_thenBothAreSetAndPersisted() {
        Long subCategoryId = 3L;
        Instant transactionDateTime = Instant.now();

        TransactionDto transactionDto = new TransactionDto();

        Transaction unsavedTransaction = createTransactionWithSubCat(subCategoryId, transactionDateTime);
        TransactionRecurrence recurrence = TransactionRecurrence.WEEKLY;
        transactionDto.setTransactionRecurrenceType(recurrence);

        RecurringTransactionDefinition recurringTransactionDefinition = new RecurringTransactionDefinition();
        BudgetCycle budgetCycle = new BudgetCycle();
        Transaction savedTransaction = new Transaction();
        TransactionDto savedTransactionDto = new TransactionDto();

        when(transactionMapper.mapToEntity(transactionDto)).thenReturn(unsavedTransaction);
        when(transactionMapper.mapToDto(savedTransaction)).thenReturn(savedTransactionDto);
        when(budgetCycleService.getBudgetCycleByInstant(subCategoryId, transactionDateTime, UserContext.getUserId()))
                .thenReturn(budgetCycle);
        when(recurringTransactionService.createRecurringTransactionDefinition(unsavedTransaction, recurrence))
                .thenReturn(recurringTransactionDefinition);
        when(transactionRepository.save(unsavedTransaction)).thenReturn(savedTransaction);

        TransactionDto actualDto = transactionService.saveTransaction(transactionDto);

        InOrder inOrder = inOrder(transactionRepository, recurringTransactionService, budgetCycleService);
        inOrder.verify(recurringTransactionService).createRecurringTransactionDefinition(unsavedTransaction, recurrence);
        inOrder.verify(budgetCycleService).getBudgetCycleByInstant(subCategoryId, transactionDateTime, UserContext.getUserId());
        inOrder.verify(transactionRepository).save(unsavedTransaction);

        assertThat(actualDto).isEqualTo(savedTransactionDto);
    }

    @Test
    public void whenSavingPastTransactionWithPastActiveBudgetCycle_thenPastBudgetCycleIsSetAndPersisted() {
        Long subCategoryId = 3L;
        Instant transactionDateTime = Instant.now().minusSeconds(60 * 60 * 24 * 60);

        TransactionDto transactionDto = new TransactionDto();

        Transaction transaction = createTransactionWithSubCat(subCategoryId, transactionDateTime);

        BudgetCycle budgetCycle = new BudgetCycle();
        Transaction savedTransaction = new Transaction();
        TransactionDto savedTransactionDto = new TransactionDto();

        when(transactionMapper.mapToEntity(transactionDto)).thenReturn(transaction);
        when(budgetCycleService.getBudgetCycleByInstant(subCategoryId, transactionDateTime, UserContext.getUserId()))
                .thenReturn(budgetCycle);
        when(transactionRepository.save(transaction)).thenReturn(savedTransaction);
        when(transactionMapper.mapToDto(savedTransaction)).thenReturn(savedTransactionDto);

        TransactionDto actualResultDto = transactionService.saveTransaction(transactionDto);

        InOrder inOrder = inOrder(transactionMapper, transactionRepository, budgetCycleService);
        inOrder.verify(transactionMapper).mapToEntity(transactionDto);
        inOrder.verify(budgetCycleService).getBudgetCycleByInstant(subCategoryId, transactionDateTime, UserContext.getUserId());
        inOrder.verify(transactionRepository).save(transaction);
        inOrder.verify(transactionMapper).mapToDto(savedTransaction);

        verify(recurringTransactionService, never())
                .createRecurringTransactionDefinition(any(Transaction.class) , any(TransactionRecurrence.class));
        assertThat(actualResultDto).isEqualTo(savedTransactionDto);
    }

    @Test
    public void whenUpdatingOtherUserTransaction_thenExceptionIsThrown() {
        UUID transactionId = UUID.randomUUID();
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transactionId);
        Transaction unsavedTransaction = new Transaction();
        unsavedTransaction.setId(transactionId);

        when(transactionMapper.mapToEntity(transactionDto)).thenReturn(unsavedTransaction);
        when(transactionRepository.existsByIdAndUserId(any(UUID.class), eq(UserContext.getUserId())))
                .thenReturn(false);

        assertThatThrownBy(() ->
                transactionService.updateTransaction(transactionDto)
        ).isInstanceOf(NoTransactionFoundException.class);
        verify(transactionRepository, never()).save(unsavedTransaction);
    }

    private Transaction createTransactionWithSubCat(Long subCategoryId, Instant transactionDateTime) {
        Transaction transaction = new Transaction();
        SubCategory subCategory = new SubCategory();
        subCategory.setId(subCategoryId);
        transaction.setSubCategory(subCategory);
        transaction.setTransactionDateTime(transactionDateTime);
        return transaction;
    }
}
