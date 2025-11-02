package com.cashigo.expensio.controller;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.service.transaction.RecurringTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/recurringtransaction")
@RequiredArgsConstructor
@StandardErrorResponses
@Tag(name = "Recurring Transactions")
public class RecurringTranDefController {

    private final RecurringTransactionService recurringTransactionService;

    @DeleteMapping(path = "/{definitionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete recurring transaction definition by ID") @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteRecurringTranDef(@PathVariable UUID definitionId) {
        recurringTransactionService.deleteRecurringTransactionDefinition(definitionId);
        return ResponseEntity.noContent().build();
    }

}
