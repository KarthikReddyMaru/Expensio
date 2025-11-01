package com.cashigo.expensio.controller;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.service.TransactionExportService;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@StandardErrorResponses
@Tag(name = "Export Transactions")
public class TransactionExportController {

    private final TransactionExportService transactionExportService;

    @GetMapping("/export")
    @Operation(summary = "Export monthly transactions") @ApiResponse(responseCode = "200")
    public void exportTransaction(int month, int year, HttpServletResponse response)
            throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        String fileName = String.format("\"transactions_%d_%d.csv\"", year, month);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename="+fileName);
        transactionExportService.exportTransactionByMonth(month, year, response);
    }

}
