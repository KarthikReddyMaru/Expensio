package com.cashigo.expensio.controller;

import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.TransactionSummaryDto;
import com.cashigo.expensio.service.TransactionSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
