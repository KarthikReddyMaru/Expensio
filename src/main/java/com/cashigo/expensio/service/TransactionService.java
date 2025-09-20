package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.TransactionDto;
import com.cashigo.expensio.dto.exception.NoTransactionFoundException;
import com.cashigo.expensio.dto.mapper.TransactionMapper;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserContext userContext;

    @Value("${page.size}")
    private int pageSize;

    @SneakyThrows
    public TransactionDto getTransactionById(UUID transactionId) {
        String userId = userContext.getUserId().orElse("Anonymous");
        Optional<Transaction> transaction = transactionRepository.findTransactionByIdAndUserId(transactionId, userId);
        Transaction data = transaction.orElseThrow(NoTransactionFoundException::new);
        return transactionMapper.mapToDto(data);
    }

    public List<TransactionDto> getAllTransactionByUserId(int pageNum) {
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        String userId = userContext.getUserId().orElse("Anonymous");
        List<Transaction> transactions = transactionRepository.findTransactionsByUserId(userId, pageRequest);
        return transactions.stream().map(transactionMapper::mapToDto).toList();
    }

    @Transactional
    public TransactionDto saveOrUpdateTransaction(TransactionDto unsavedTransaction) {
        Transaction transaction = transactionMapper.mapToEntity(unsavedTransaction);
        String userId = userContext.getUserId().orElse("Anonymous");
        transaction.setUserId(userId);
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction of {} saved with id {}", userContext.getUserName().orElse("Anonymous"), savedTransaction.getId());
        return transactionMapper.mapToDto(savedTransaction);
    }

    @Transactional
    public void deleteTransaction(UUID transactionId) {
        String userId = userContext.getUserId().orElse("Anonymous");
        log.info("Transaction of {} is deleted with id {}", userContext.getUserName().orElse("Anonymous"), transactionId);
        transactionRepository.deleteByIdAndUserId(transactionId, userId);
    }

}
