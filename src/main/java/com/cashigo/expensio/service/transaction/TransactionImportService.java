package com.cashigo.expensio.service.transaction;

import com.cashigo.expensio.common.consts.Status;
import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.common.util.*;
import com.cashigo.expensio.dto.ImportErrorDto;
import com.cashigo.expensio.dto.projection.BudgetDefCacheProjection;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.cashigo.expensio.model.*;
import com.cashigo.expensio.repository.*;
import com.cashigo.expensio.service.category.CategoryImportService;
import com.cashigo.expensio.service.file.FileStorageService;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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
    private final FileStorageService fileStorageService;
    private final ImportMetaDataRepository importMetaDataRepository;

    @Transactional
    public ImportMetaData parseTransactions(MultipartFile multipartFile) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        if (multipartFile == null || multipartFile.isEmpty())
            throw new IllegalArgumentException("Invalid File");

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


        FileMetaData fileMetaData = null;
        if (!errorMessages.isEmpty()) {
            String fileName = String.format("failed_transactions_%s.csv", LocalDate.now());
            byte[] errorCsvBytes = generateErrorCSV(errorMessages);
            fileMetaData = fileStorageService.storeFile(errorCsvBytes, fileName);
        }

        long transactionsFailed = errorMessages.size();
        long subCategoriesCreated = createNonExistedCategories(transactions);
        long transactionsSaved = createTransactions(transactions);

        ImportMetaData importMetaData = ImportMetaData
                .builder()
                .userId(UserContext.getUserId())
                .errorFile(fileMetaData)
                .transactionsSaved(transactionsSaved)
                .transactionsFailed(transactionsFailed)
                .subCategoriesCreated(subCategoriesCreated)
                .status(getStatus(transactionsSaved, transactionsFailed, subCategoriesCreated))
                .totalRecords(transactionsSaved + transactionsFailed)
                .build();

        return importMetaDataRepository.save(importMetaData);
    }

    @Transactional
    long createTransactions(List<TransactionSummaryDto> records) {

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
        long savedTransactions = transactionRepository.saveAll(transactions).size();
        BudgetCycleUtil.clearCache();
        CategoryUtil.clearCache();

        return savedTransactions;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    long createNonExistedCategories(List<TransactionSummaryDto> records) {

        Set<Map.Entry<String, List<TransactionSummaryDto>>> entries = records.stream()
                .collect(Collectors.groupingBy(TransactionSummaryDto::getCategory))
                .entrySet();

        HashMap<String, List<String>> categories = new HashMap<>();

        for (Map.Entry<String, List<TransactionSummaryDto>> entry: entries) {
            List<String> subCategories = entry.getValue().stream().map(TransactionSummaryDto::getSubCategory).toList();
            categories.put(entry.getKey(), subCategories);
        }

        return categoryImportService.createNonExistedCategories(categories);
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

    private byte[] generateErrorCSV(List<ImportErrorDto> errorMessages) throws
            IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8)) {
            new StatefulBeanToCsvBuilder<ImportErrorDto>(outputStreamWriter)
                    .withMappingStrategy(CsvUtil.getErrorStrategy())
                    .withApplyQuotesToAll(false)
                    .build()
                    .write(errorMessages);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private Status.ImportStatus getStatus(long transactionsSaved, long transactionsFailed, long subCategoriesCreated) {
        if (transactionsSaved == 0)
            return Status.ImportStatus.FAILURE;
        else if (transactionsFailed > 0)
            return Status.ImportStatus.PARTIAL_SUCCESS;
        return Status.ImportStatus.SUCCESS;
    }

}
