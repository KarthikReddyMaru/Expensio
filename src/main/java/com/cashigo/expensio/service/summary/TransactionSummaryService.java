package com.cashigo.expensio.service.summary;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.cashigo.expensio.dto.summary.mapper.TransactionToSummaryMapper;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Page<Transaction> transactions = transactionRepository.findTransactionsByWithSubCategory(userId, pageable);
        return transactions.stream().map(transactionToSummaryMapper::map).toList();
    }

}
