package com.cashigo.expensio.controller;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.common.validation.OnCreate;
import com.cashigo.expensio.common.validation.OnUpdate;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.TransactionDto;
import com.cashigo.expensio.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Transaction")
@StandardErrorResponses
@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping(path = "/{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Transaction By Transaction Id")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<TransactionDto>> getTransactionById(@PathVariable UUID transactionId) {
        Response<TransactionDto> response = new Response<>();
        TransactionDto transactionDto = transactionService.getTransactionById(transactionId);
        response.setData(transactionDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = {"pageNum"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns recent transactions based on PageNum")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<List<TransactionDto>>> getTransactionByUserId(int pageNum) {
        Response<List<TransactionDto>> response = new Response<>();
        List<TransactionDto> transactions = transactionService.getAllTransactionByUserId(pageNum);
        response.setData(transactions);
        return ResponseEntity.ok(response);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Save transaction") @ApiResponse(responseCode = "201")
    public ResponseEntity<Response<TransactionDto>> saveTransaction(@Validated(OnCreate.class)
                                                                        @RequestBody TransactionDto transactionDto) {
        Response<TransactionDto> response = new Response<>();
        TransactionDto savedTransactionDto = transactionService.saveTransaction(transactionDto);
        response.setData(savedTransactionDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update transaction") @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<TransactionDto>> updateTransaction(@Validated(OnUpdate.class)
                                                                          @RequestBody TransactionDto transactionDto) {
        Response<TransactionDto> response = new Response<>();
        TransactionDto savedTransactionDto = transactionService.updateTransaction(transactionDto);
        response.setData(savedTransactionDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{transactionId}")
    @Operation(summary = "Delete transaction") @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID transactionId) {
        transactionService.deleteTransaction(transactionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
