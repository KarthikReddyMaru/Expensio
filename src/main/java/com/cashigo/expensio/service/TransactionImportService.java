package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.common.util.*;
import com.cashigo.expensio.dto.ImportErrorDto;
import com.cashigo.expensio.dto.projection.BudgetDefCacheProjection;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.cashigo.expensio.model.BudgetCycle;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.BudgetCycleRepository;
import com.cashigo.expensio.repository.BudgetDefinitionRepository;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionImportService {

    private final Validator validator;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryImportService categoryImportService;
    private final BudgetDefinitionRepository budgetDefinitionRepository;
    private final BudgetCycleRepository budgetCycleRepository;

    @Transactional
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

        if (!transactions.isEmpty())
            createTransactions(transactions);

        return true;
    }

    @Transactional
    void createTransactions(List<TransactionSummaryDto> records) {

        createNonExistedCategories(records);

        List<Category> categories = categoryRepository
                .findCategoriesOfUserWithSubCategories(UserContext.getUserId(), Sort.by("name"));
        List<BudgetDefCacheProjection> defCacheProjections = budgetDefinitionRepository
                .findBudgetDefinitionsForCacheByUserId(UserContext.getUserId());

        CategoryUtil.createCategoryCache(categories);
        BudgetCycleUtil.createCache(defCacheProjections);
        List<Transaction> transactions = records
                .stream()
                .map(this::mapTransaction)
                .toList();
        transactionRepository.saveAll(transactions);
        BudgetCycleUtil.clearCache();
        CategoryUtil.clearCache();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void createNonExistedCategories(List<TransactionSummaryDto> records) {

        Set<Map.Entry<String, List<TransactionSummaryDto>>> entries = records.stream()
                .collect(Collectors.groupingBy(TransactionSummaryDto::getCategory))
                .entrySet();

        HashMap<String, List<String>> categories = new HashMap<>();

        for (Map.Entry<String, List<TransactionSummaryDto>> entry: entries) {
            List<String> subCategories = entry.getValue().stream().map(TransactionSummaryDto::getSubCategory).toList();
            categories.put(entry.getKey(), subCategories);
        }

        categoryImportService.createNonExistedCategories(categories);
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

    private Transaction mapTransaction(TransactionSummaryDto dto) {

        Instant transactionTime = ZoneUtil.toInstant(dto.getTransactionDate(), dto.getTransactionTime());
        Category category = CategoryUtil.getCategory(dto.getCategory());
        SubCategory subCategory = CategoryUtil.getSubCategory(dto.getCategory(), dto.getSubCategory());

        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setUserId(UserContext.getUserId());
        transaction.setTransactionDateTime(transactionTime);
        transaction.setNote(dto.getNote());
        transaction.setSubCategory(subCategory);
        assert category != null;
        transaction.setBudgetCycle(getBudgetCycle(category.getId(), transactionTime));
        return transaction;
    }

    private BudgetCycle getBudgetCycle(Long categoryId, Instant transactionTime) {
        UUID budgetCycleByCategory = BudgetCycleUtil.getBudgetCycleByCategory(categoryId, transactionTime);
        if (budgetCycleByCategory != null)
            return budgetCycleRepository.getReferenceById(budgetCycleByCategory);
        return null;
    }

}
