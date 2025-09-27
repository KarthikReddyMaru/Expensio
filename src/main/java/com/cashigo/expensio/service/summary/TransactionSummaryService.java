package com.cashigo.expensio.service.summary;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.exception.NoTransactionFoundException;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.cashigo.expensio.dto.summary.mapper.TransactionToSummaryMapper;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionSummaryService {

    private final TransactionRepository transactionRepository;
    private final TransactionToSummaryMapper transactionToSummaryMapper;
    private final UserContext userContext;

    @Value("${page.size}")
    private int pageSize;

    public List<TransactionSummaryDto> getAllTransactionSummaryByUserId(int pageNum) {
        String userId = userContext.getUserId();
        Sort sort = Sort.by("transactionDateTime").descending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        Page<Transaction> transactions = transactionRepository.findTransactionsOfUserWithSubCategories(userId, pageable);
        return transactions.stream().map(transactionToSummaryMapper::map).toList();
    }

    @SneakyThrows
    public TransactionSummaryDto getTransactionSummaryById(UUID transactionId) {
        String userId = userContext.getUserId();
        Transaction transaction = transactionRepository
                .findTransactionByIdWithSubCat(transactionId, userId)
                .orElseThrow(NoTransactionFoundException::new);
        return transactionToSummaryMapper.map(transaction);
    }

}
