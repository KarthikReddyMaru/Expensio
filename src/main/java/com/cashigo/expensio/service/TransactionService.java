package com.cashigo.expensio.service;

import com.cashigo.expensio.dto.TransactionDto;
import com.cashigo.expensio.dto.exception.NoTransactionFoundException;
import com.cashigo.expensio.dto.mapper.TransactionMapper;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Value("${page.size}")
    private int pageSize;

    @SneakyThrows
    public TransactionDto getTransactionById(UUID transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        Transaction data = transaction.orElseThrow(NoTransactionFoundException::new);
        return transactionMapper.mapToDto(data);
    }

    public List<TransactionDto> getAllTransactionByUserId(String userId, int pageNum) {
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        List<Transaction> transactions = transactionRepository.findTransactionsByUserId(userId, pageRequest);
        return transactions.stream().map(transactionMapper::mapToDto).toList();
    }

    public TransactionDto saveOrUpdateTransaction(TransactionDto unsavedTransaction) {
        Transaction transaction = transactionMapper.mapToEntity(unsavedTransaction);
        transaction.setUserId("SomeUserIdForNow");
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.mapToDto(savedTransaction);
    }

    public void deleteTransaction(UUID transactionId) {
        transactionRepository.deleteById(transactionId);
    }

}
