package com.cashigo.expensio.controller.summary;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.summary.BudgetCycleSummaryDto;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.cashigo.expensio.service.summary.BudgetCycleSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("summary/budgetcycle")
@RequiredArgsConstructor
@StandardErrorResponses
@Tag(name = "Budget Cycle - Summary")
public class BudgetCycleSummaryController {

    private final BudgetCycleSummaryService budgetCycleSummaryService;

    @GetMapping("/{budgetCycleId}")
    public ResponseEntity<Response<BudgetCycleSummaryDto>> getBudgetCycleSummary(@PathVariable UUID budgetCycleId) {
        Response<BudgetCycleSummaryDto> response = new Response<>();
        BudgetCycleSummaryDto summary = budgetCycleSummaryService.getBudgetCycleSummaryById(budgetCycleId);
        response.setData(summary);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transaction/{budgetCycleId}")
    public ResponseEntity<Response<List<TransactionSummaryDto>>> getBudgetCycleTransactions(@PathVariable UUID budgetCycleId) {
        Response<List<TransactionSummaryDto>> response = new Response<>();
        List<TransactionSummaryDto> summary = budgetCycleSummaryService.getTransactionsInCycle(budgetCycleId);
        response.setData(summary);
        return ResponseEntity.ok(response);
    }

}
