package com.cashigo.expensio.controller.summary;

import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.summary.BudgetCycleSummaryDto;
import com.cashigo.expensio.service.summary.BudgetCycleSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/budgetcycle/summary")
@RequiredArgsConstructor
public class BudgetCycleSummaryController {

    private final BudgetCycleSummaryService budgetCycleSummaryService;

    @GetMapping("/{budgetCycleId}")
    public ResponseEntity<Response<BudgetCycleSummaryDto>> getBudgetCycleSummary(@PathVariable UUID budgetCycleId) {
        Response<BudgetCycleSummaryDto> response = new Response<>();
        BudgetCycleSummaryDto summary = budgetCycleSummaryService.getBudgetCycleSummaryById(budgetCycleId);
        response.setData(summary);
        return ResponseEntity.ok(response);
    }

}
