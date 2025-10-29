package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.common.util.*;
import com.cashigo.expensio.dto.ImportErrorDto;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.CategoryRepository;
import com.cashigo.expensio.repository.TransactionRepository;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionImportService {

    private final Validator validator;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public boolean parseTransactions(MultipartFile multipartFile, HttpServletResponse response) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        if (multipartFile == null || multipartFile.isEmpty())
            return false;

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

            if (!transactions.isEmpty())
                createTransactions(transactions);

            return false;
        }


        return true;
    }

    private void createTransactions(List<TransactionSummaryDto> records) {

        List<Category> categories = categoryRepository
                .findCategoriesOfUserWithSubCategories(UserContext.getUserId(), Sort.by("name"));

        CategoryUtil.createCategoryCache(categories);
        List<Transaction> transactions = records
                .stream()
                .map(TransactionImportService::mapTransaction)
                .toList();
        transactionRepository.saveAll(transactions);
        CategoryUtil.clearCache();
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

    private static Transaction mapTransaction(TransactionSummaryDto dto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setUserId(UserContext.getUserId());
        transaction.setTransactionDateTime(ZoneUtil.toInstant(dto.getTransactionDate(), dto.getTransactionTime()));
        transaction.setNote(dto.getNote());
        transaction.setSubCategory(CategoryUtil.getSubCategory(dto.getCategory(), dto.getSubCategory()));
        return transaction;
    }

}
