package com.cashigo.expensio.controller.summary;

import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.cashigo.expensio.service.summary.TransactionSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transaction/summary")
@RequiredArgsConstructor
public class TransactionSummaryController {

    private final TransactionSummaryService transactionSummaryService;

    @GetMapping(params = {"pageNum"})
    public ResponseEntity<Response<List<TransactionSummaryDto>>> getAllTransactionSummaryByUserId(int pageNum) {
        Response<List<TransactionSummaryDto>> response = new Response<>();
        List<TransactionSummaryDto> transactionSummary = transactionSummaryService.getAllTransactionSummaryByUserId(pageNum);
        response.setData(transactionSummary);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Response<TransactionSummaryDto>> getTransactionSummaryById(@PathVariable UUID transactionId) {
        Response<TransactionSummaryDto> response = new Response<>();
        TransactionSummaryDto transactionSummary = transactionSummaryService.getTransactionSummaryById(transactionId);
        response.setData(transactionSummary);
        return ResponseEntity.ok(response);
    }

}
