package com.cashigo.expensio.service;

import com.cashigo.expensio.common.util.CsvUtil;
import com.cashigo.expensio.common.util.ErrorUtil;
import com.cashigo.expensio.dto.ImportErrorDto;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionImportService {

    private final Validator validator;

    public void createTransactions(MultipartFile multipartFile, HttpServletResponse response) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        if (multipartFile.isEmpty())
            return;

        List<ImportErrorDto> errorMessages = new ArrayList<>();
        List<TransactionSummaryDto> transactions = CsvUtil.parseCSV(multipartFile, errorMessages);

        transactions = transactions.stream().filter(transaction -> {
            String message = ErrorUtil.parseErrorMessages(validator.validateObject(transaction));
            if (!message.isEmpty()) {
                errorMessages.add(buildImportError(message, transaction));
                return false;
            }
            return true;
        }).toList();

        if (!errorMessages.isEmpty()) {
            String fileName = String.format("\"failed_transactions_%s.csv\"", LocalDate.now());
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            new StatefulBeanToCsvBuilder<ImportErrorDto>(response.getWriter())
                    .withMappingStrategy(CsvUtil.getErrorStrategy())
                    .withApplyQuotesToAll(false)
                    .build()
                    .write(errorMessages);
        }

        transactions.forEach(transaction -> log.info("{}", transaction));
    }

    private ImportErrorDto buildImportError(String reason, TransactionSummaryDto transaction) {
        return ImportErrorDto
                .builder()
                .reason(reason)
                .transactionDate(transaction.getTransactionDate().toString())
                .transactionTime(transaction.getTransactionTime().toString())
                .category(transaction.getCategory())
                .subCategory(transaction.getSubCategory())
                .amount(transaction.getAmount().toString())
                .note(transaction.getNote())
                .build();

    }
}
