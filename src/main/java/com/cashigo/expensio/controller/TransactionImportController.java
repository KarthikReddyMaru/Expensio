package com.cashigo.expensio.controller;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.model.ImportMetaData;
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
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Import transactions with csv file",
            description = "Headers: DATE,TIME,CATEGORY,SUBCATEGORY,AMOUNT,NOTE : TIME & NOTE are optional"
    )
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<ImportMetaData>> importTransactions(@RequestPart("file") MultipartFile file)
            throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        ImportMetaData importMetaData = importService.parseTransactions(file);
        Response<ImportMetaData> response = new Response<>();
        response.setData(importMetaData);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
