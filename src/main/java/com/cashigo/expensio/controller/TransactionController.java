package com.cashigo.expensio.controller;

import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.TransactionDto;
import com.cashigo.expensio.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{transactionId}")
    public ResponseEntity<Response<TransactionDto>> getTransactionById(@PathVariable UUID transactionId) {
        Response<TransactionDto> response = new Response<>();
        TransactionDto transactionDto = transactionService.getTransactionById(transactionId);
        response.setData(transactionDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = {"pageNum"})
    public ResponseEntity<Response<List<TransactionDto>>> getTransactionByUserId(int pageNum) {
        Response<List<TransactionDto>> response = new Response<>();
        List<TransactionDto> transactions = transactionService.getAllTransactionByUserId(pageNum);
        response.setData(transactions);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<TransactionDto>> saveTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        Response<TransactionDto> response = new Response<>();
        TransactionDto savedTransactionDto = transactionService.saveTransaction(transactionDto);
        response.setData(savedTransactionDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Response<TransactionDto>> updateTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        Response<TransactionDto> response = new Response<>();
        TransactionDto savedTransactionDto = transactionService.updateTransaction(transactionDto);
        response.setData(savedTransactionDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID transactionId) {
        transactionService.deleteTransaction(transactionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
