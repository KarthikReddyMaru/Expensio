package com.cashigo.expensio.controller;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.service.transaction.TransactionImportService;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@StandardErrorResponses
@Tag(name = "Import Transactions")
public class TransactionImportController {

    private final TransactionImportService importService;

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import transactions with csv file",
            description = "Headers: DATE,TIME,CATEGORY,SUBCATEGORY,AMOUNT,NOTE : TIME & NOTE are optional"
    )
    @ApiResponse(responseCode = "200")
    public void importTransactions(@RequestPart("file") MultipartFile file, HttpServletResponse response) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        boolean flag = importService.parseTransactions(file, response);
        if (flag) {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("Records processed successfully");
        }
    }

}
