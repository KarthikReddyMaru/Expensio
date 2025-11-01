package com.cashigo.expensio.service;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.exception.InvalidRecurrenceTransactionException;
import com.cashigo.expensio.model.RecurringTransactionDefinition;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.RecurringTransactionDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecurringTransactionServiceTest {

    @Mock
    private RecurringTransactionDefinitionRepository transactionDefinitionRepository;

    @InjectMocks
    private RecurringTransactionService recurringTransactionService;

    @BeforeEach
    public void init() {
        String currentLoggedInUserId = UUID.randomUUID().toString();
        try(MockedStatic<UserContext> mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getUserId).thenReturn(currentLoggedInUserId);
        }
        recurringTransactionService.setZone("Asia/Kolkata");
    }


    @ParameterizedTest
    @EnumSource(TransactionRecurrence.class)
    void whenCreatingRecurringTransactionDefinitionWithPresentOrFutureDate_thenItIsPersisted(TransactionRecurrence transactionRecurrence) {
        Transaction transaction = new Transaction();
        Instant transactionDateTime = Instant.now();
        BigDecimal amount = BigDecimal.valueOf(2000L);
        transaction.setAmount(amount);
        transaction.setUserId(UserContext.getUserId());
        transaction.setTransactionDateTime(transactionDateTime);

        RecurringTransactionDefinition recurringTransactionDefinition =
                new RecurringTransactionDefinition();

        LocalDate nextOccurrence;
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");

        switch (transactionRecurrence) {
            case DAILY -> nextOccurrence = transactionDateTime.atZone(zoneId).plusDays(1).toLocalDate();
            case WEEKLY -> nextOccurrence = transactionDateTime.atZone(zoneId).plusWeeks(1).toLocalDate();
            default -> {
                return;
            }
        }

        when(transactionDefinitionRepository.save(argThat(definition ->
                definition.getNextOccurrence().isEqual(nextOccurrence) &&
                definition.getTransactionRecurrenceType().equals(transactionRecurrence) &&
                definition.getAmount().equals(amount) &&
                definition.getUserId().equals(UserContext.getUserId()) &&
                definition.getLastProcessedInstant().equals(transactionDateTime)
        ))).thenReturn(recurringTransactionDefinition);

        RecurringTransactionDefinition actualDefinition = recurringTransactionService.createRecurringTransactionDefinition(transaction, transactionRecurrence);

        verify(transactionDefinitionRepository).save(any(RecurringTransactionDefinition.class));
        assertThat(actualDefinition).isEqualTo(recurringTransactionDefinition);
    }

    @ParameterizedTest
    @EnumSource(TransactionRecurrence.class)
    void whenCreatingRecurringTransactionDefinitionWithPastDate_thenExceptionIsThrown(TransactionRecurrence transactionRecurrence) {
        Transaction transaction = new Transaction();
        Instant transactionDateTime = Instant.now().minusSeconds(60 * 60 * 24);
        BigDecimal amount = BigDecimal.valueOf(2000L);
        transaction.setAmount(amount);
        transaction.setUserId(UserContext.getUserId());
        transaction.setTransactionDateTime(transactionDateTime);

        assertThatThrownBy(() ->
                recurringTransactionService.createRecurringTransactionDefinition(transaction, transactionRecurrence)
        ).isInstanceOf(InvalidRecurrenceTransactionException.class);

        verify(transactionDefinitionRepository, never()).save(any(RecurringTransactionDefinition.class));
    }
}
