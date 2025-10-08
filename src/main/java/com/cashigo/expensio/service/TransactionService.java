package com.cashigo.expensio.service;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.TransactionDto;
import com.cashigo.expensio.dto.exception.NoTransactionFoundException;
import com.cashigo.expensio.dto.mapper.TransactionMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.RecurringTransactionDefinition;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final RecurringTransactionService recurringTransactionService;
    private final BudgetCycleService budgetCycleService;
    private final UserContext userContext;

    @Setter
    @Value("${page.size}")
    private int pageSize;

    @SneakyThrows
    public TransactionDto getTransactionById(UUID transactionId) {
        String userId = userContext.getUserId();
        Optional<Transaction> transaction = transactionRepository.findTransactionById(transactionId, userId);
        Transaction data = transaction.orElseThrow(NoTransactionFoundException::new);
        return transactionMapper.mapToDto(data);
    }

    public List<TransactionDto> getAllTransactionByUserId(int pageNum) {
        Sort sort = Sort.by("transactionDateTime").descending();
        Pageable pageRequest = PageRequest.of(pageNum, pageSize, sort);
        String userId = userContext.getUserId();
        Page<Transaction> transactions = transactionRepository.findTransactionsOfUserWithSubCategories(userId, pageRequest);
        return transactions.stream().map(transactionMapper::mapToDto).toList();
    }

    @Transactional
    public TransactionDto saveTransaction(TransactionDto unsavedTransaction) {

        Transaction transaction = transactionMapper.mapToEntity(unsavedTransaction);
        String userId = userContext.getUserId();
        transaction.setUserId(userId);

        TransactionRecurrence transactionRecurrence = unsavedTransaction.getTransactionRecurrenceType();
        if (transactionRecurrence != null && !transactionRecurrence.equals(TransactionRecurrence.NONE))
            setRecurringTransaction(transactionRecurrence, transaction);

        if (transaction.getSubCategory() != null)
            setBudgetCycle(transaction, userId);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.mapToDto(savedTransaction);
    }

    @Transactional
    @SneakyThrows
    public TransactionDto updateTransaction(TransactionDto transactionDto) {

        Transaction transaction = transactionMapper.mapToEntity(transactionDto);
        String userId = userContext.getUserId();
        transaction.setUserId(userId);

        boolean isTransactionBelongsToUser =
                transactionRepository.existsByIdAndUserId(transaction.getId(), userId);
        if (!isTransactionBelongsToUser)
            throw new NoTransactionFoundException();

        if (transaction.getSubCategory() != null)
            setBudgetCycle(transaction, userId);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.mapToDto(savedTransaction);
    }

    @Transactional
    public void deleteTransaction(UUID transactionId) {
        String userId = userContext.getUserId();
        log.info("Transaction of {} is deleted with id {}", userContext.getUserName(), transactionId);
        transactionRepository.deleteByIdAndUserId(transactionId, userId);
    }

    public void setBudgetCycle(Transaction transaction, String userId) {
        Long subCategoryId = transaction.getSubCategory().getId();
        Instant transactionInstant = transaction.getTransactionDateTime();
        BudgetCycle budgetCycle = budgetCycleService.getBudgetCycleByInstant(subCategoryId, transactionInstant, userId);
        transaction.setBudgetCycle(budgetCycle);
    }

    public void setRecurringTransaction(TransactionRecurrence transactionRecurrence, Transaction transaction) {
        RecurringTransactionDefinition recurringTransactionDefinition =
                recurringTransactionService.createRecurringTransactionDefinition(transaction, transactionRecurrence);
        transaction.setTransactionDefinition(recurringTransactionDefinition);
    }

}
