package com.cashigo.expensio.service.transaction;

import com.cashigo.expensio.common.consts.TransactionRecurrence;
import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.TransactionDto;
import com.cashigo.expensio.dto.exception.NoTransactionFoundException;
import com.cashigo.expensio.dto.mapper.TransactionMapper;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.RecurringTransactionDefinition;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import com.cashigo.expensio.service.budget.BudgetCycleService;
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

    @Setter
    @Value("${page.size}")
    private int pageSize;

    @SneakyThrows
    public TransactionDto getTransactionById(UUID transactionId) {
        Optional<Transaction> transaction = transactionRepository.findTransactionById(transactionId, UserContext.getUserId());
        Transaction data = transaction.orElseThrow(NoTransactionFoundException::new);
        return transactionMapper.mapToDto(data);
    }

    public List<TransactionDto> getAllTransactionByUserId(int pageNum) {
        Sort sort = Sort.by("transactionDateTime").descending();
        Pageable pageRequest = PageRequest.of(pageNum, pageSize, sort);
        Page<Transaction> transactions = transactionRepository.findTransactionsOfUserWithSubCategories(UserContext.getUserId(), pageRequest);
        return transactions.stream().map(transactionMapper::mapToDto).toList();
    }

    @Transactional
    public TransactionDto saveTransaction(TransactionDto unsavedTransaction) {

        Transaction transaction = transactionMapper.mapToEntity(unsavedTransaction);
        transaction.setUserId(UserContext.getUserId());

        TransactionRecurrence transactionRecurrence = unsavedTransaction.getTransactionRecurrenceType();
        if (transactionRecurrence != null && !transactionRecurrence.equals(TransactionRecurrence.NONE))
            setRecurringTransaction(transactionRecurrence, transaction);

        if (transaction.getSubCategory() != null)
            setBudgetCycle(transaction);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.mapToDto(savedTransaction);
    }

    @Transactional
    @SneakyThrows
    public TransactionDto updateTransaction(TransactionDto transactionDto) {

        Transaction transaction = transactionMapper.mapToEntity(transactionDto);
        transaction.setUserId(UserContext.getUserId());

        boolean isTransactionBelongsToUser =
                transactionRepository.existsByIdAndUserId(transaction.getId(), UserContext.getUserId());
        if (!isTransactionBelongsToUser)
            throw new NoTransactionFoundException();

        if (transaction.getSubCategory() != null)
            setBudgetCycle(transaction);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.mapToDto(savedTransaction);
    }

    @Transactional
    public void deleteTransaction(UUID transactionId) {
        transactionRepository.deleteByIdAndUserId(transactionId, UserContext.getUserId());
    }

    public void setBudgetCycle(Transaction transaction) {
        Long subCategoryId = transaction.getSubCategory().getId();
        Instant transactionInstant = transaction.getTransactionDateTime();
        BudgetCycle budgetCycle = budgetCycleService.getBudgetCycleByInstant(subCategoryId, transactionInstant, UserContext.getUserId());
        transaction.setBudgetCycle(budgetCycle);
    }

    public void setRecurringTransaction(TransactionRecurrence transactionRecurrence, Transaction transaction) {
        RecurringTransactionDefinition recurringTransactionDefinition =
                recurringTransactionService.createRecurringTransactionDefinition(transaction, transactionRecurrence);
        transaction.setTransactionDefinition(recurringTransactionDefinition);
    }

}
