package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.TransactionSummaryDto;
import com.cashigo.expensio.dto.mapper.TransactionSummaryMapper;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionSummaryService {

    private final TransactionRepository transactionRepository;
    private final TransactionSummaryMapper transactionSummaryMapper;
    private final UserContext userContext;

    @Value("${page.size}")
    private int pageSize;

    public List<TransactionSummaryDto> getAllTransactionSummaryByUserId(int pageNum) {
        String userId = userContext.getUserId();
        Sort sort = Sort.by("transactionDateTime").descending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        List<Transaction> transactions = transactionRepository.findTransactionsByUserId(userId, pageable);
        return transactions.stream().map(transactionSummaryMapper::map).toList();
    }

}
