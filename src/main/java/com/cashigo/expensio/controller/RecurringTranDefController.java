package com.cashigo.expensio.controller;

import com.cashigo.expensio.service.RecurringTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/recurringtransaction")
@RequiredArgsConstructor
public class RecurringTranDefController {

    private final RecurringTransactionService recurringTransactionService;

    @DeleteMapping("/{definitionId}")
    public ResponseEntity<Void> deleteRecurringTranDef(@PathVariable UUID definitionId) {
        recurringTransactionService.deleteRecurringTransactionDefinition(definitionId);
        return ResponseEntity.noContent().build();
    }

}
