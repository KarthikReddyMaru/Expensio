package com.cashigo.expensio.service;

import com.cashigo.expensio.dto.TransactionSummaryDto;
import com.cashigo.expensio.dto.mapper.TransactionSummaryMapper;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionSummaryService {

    private final TransactionRepository transactionRepository;
    private final TransactionSummaryMapper transactionSummaryMapper;

    @Value("${page.size}")
    private int pageSize;

    public List<TransactionSummaryDto> getAllTransactionSummaryByUserId(String userId, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum,pageSize);
        List<Transaction> transactions = transactionRepository.findTransactionsByUserId(userId, pageable);
        return transactions.stream().map(transactionSummaryMapper::map).toList();
    }

}
