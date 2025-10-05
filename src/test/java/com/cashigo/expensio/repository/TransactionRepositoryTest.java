package com.cashigo.expensio.repository;

import com.cashigo.expensio.dto.exception.NoTransactionFoundException;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    private String currentLoggedInUserId;

    @BeforeEach
    void init() {
        currentLoggedInUserId = UUID.randomUUID().toString();
    }

    @Test
    void whenSavingPresentTransaction_thenItIsPersisted() {
        Instant transactionTime = Instant.now();
        BigDecimal amount = BigDecimal.valueOf(100L);
        Transaction transaction = createAndSaveTransaction(transactionTime, amount, currentLoggedInUserId);
        Transaction fetchedTransaction = transactionRepository
                .findTransactionById(transaction.getId(), currentLoggedInUserId)
                .orElseThrow();

        assertThat(fetchedTransaction.getSubCategory().getId()).isNotNull();
        assertThat(fetchedTransaction.getAmount()).isEqualTo(amount);
        assertThat(fetchedTransaction.getTransactionDateTime()).isEqualTo(transactionTime);
        assertThat(fetchedTransaction.getUserId()).isEqualTo(currentLoggedInUserId);
        assertThat(fetchedTransaction.getNote()).as("Note").isEqualTo("NOTE");
        assertThat(fetchedTransaction.getCreatedAt()).as("Created At").isNotNull();
        assertThat(fetchedTransaction.getUpdatedAt()).as("Updated At").isEqualTo(fetchedTransaction.getCreatedAt());
    }

    @Test
    void whenSavingPastTransaction_thenItIsPersisted() {
        Instant transactionTime = Instant.now().minusSeconds(60 * 60 * 24 * 2);
        BigDecimal amount = BigDecimal.valueOf(100L);
        Transaction transaction = createAndSaveTransaction(transactionTime, amount, currentLoggedInUserId);
        Transaction fetchedTransaction = transactionRepository
                .findTransactionById(transaction.getId(), currentLoggedInUserId)
                .orElseThrow();

        assertThat(fetchedTransaction.getSubCategory().getId()).isNotNull();
        assertThat(fetchedTransaction.getAmount()).isEqualTo(amount);
        assertThat(fetchedTransaction.getTransactionDateTime()).isEqualTo(transactionTime);
        assertThat(fetchedTransaction.getUserId()).isEqualTo(currentLoggedInUserId);
        assertThat(fetchedTransaction.getNote()).as("Note").isEqualTo("NOTE");
        assertThat(fetchedTransaction.getCreatedAt()).as("Created At").isNotNull();
        assertThat(fetchedTransaction.getUpdatedAt()).as("Updated At").isEqualTo(fetchedTransaction.getCreatedAt());
    }

    @Test
    void whenSavingFutureTransaction_thenItIsPersisted() {
        Instant transactionTime = Instant.now().plusSeconds(60 * 60 * 24 * 2);
        BigDecimal amount = BigDecimal.valueOf(100L);
        Transaction transaction = createAndSaveTransaction(transactionTime, amount, currentLoggedInUserId);
        Transaction fetchedTransaction = transactionRepository
                .findTransactionById(transaction.getId(), currentLoggedInUserId)
                .orElseThrow();

        assertThat(fetchedTransaction.getSubCategory().getId()).isNotNull();
        assertThat(fetchedTransaction.getAmount()).isEqualTo(amount);
        assertThat(fetchedTransaction.getTransactionDateTime()).isEqualTo(transactionTime);
        assertThat(fetchedTransaction.getUserId()).isEqualTo(currentLoggedInUserId);
        assertThat(fetchedTransaction.getNote()).as("Note").isEqualTo("NOTE");
        assertThat(fetchedTransaction.getCreatedAt()).as("Created At").isNotNull();
        assertThat(fetchedTransaction.getUpdatedAt()).as("Updated At").isEqualTo(fetchedTransaction.getCreatedAt());
    }

    @Test
    void whenFetchingOtherUserTransaction_thenExceptionIsThrown() {
        String otherUserId = UUID.randomUUID().toString();
        Instant transactionTime = Instant.now();
        BigDecimal amount = BigDecimal.valueOf(100L);
        Transaction transaction = createAndSaveTransaction(transactionTime, amount, otherUserId);
        assertThatThrownBy(() ->
                transactionRepository
                        .findTransactionById(transaction.getId(), currentLoggedInUserId)
                        .orElseThrow(NoTransactionFoundException::new)
        ).isInstanceOf(NoTransactionFoundException.class);
    }

    @Test
    void whenUpdatingTransaction_thenItIsUpdated() {
        Instant transactionTime = Instant.now();
        BigDecimal amount = BigDecimal.valueOf(100L);
        Transaction transaction = createAndSaveTransaction(transactionTime, amount, currentLoggedInUserId);

        Instant updatedTransactionTime = Instant.now().minusSeconds(60 * 60 * 24 * 6);
        BigDecimal updatedAmount = BigDecimal.valueOf(1000L);
        transaction.setAmount(updatedAmount);
        transaction.setTransactionDateTime(updatedTransactionTime);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        Transaction fetchUpdatedTransaction = transactionRepository
                .findTransactionById(updatedTransaction.getId(), currentLoggedInUserId)
                .orElseThrow();

        assertThat(fetchUpdatedTransaction.getUpdatedAt()).isAfter(fetchUpdatedTransaction.getCreatedAt());
        assertThat(fetchUpdatedTransaction)
                .extracting(Transaction::getTransactionDateTime, Transaction::getAmount)
                .containsExactly(updatedTransactionTime, updatedAmount);
    }

    @Test
    void whenDeletingTransaction_thenItIsRemovedFromDB() {
        Instant transactionTime = Instant.now();
        BigDecimal amount = BigDecimal.valueOf(100L);
        Transaction transaction = createAndSaveTransaction(transactionTime, amount, currentLoggedInUserId);

        assertThat(transactionRepository
                .findTransactionById(transaction.getId(), currentLoggedInUserId))
                .isPresent()
                .get().extracting(Transaction::getId).isNotNull();

        transactionRepository.deleteByIdAndUserId(transaction.getId(), currentLoggedInUserId);

        assertThatThrownBy(() ->
                transactionRepository
                        .findTransactionById(transaction.getId(), currentLoggedInUserId)
                        .orElseThrow(NoTransactionFoundException::new)
        ).isInstanceOf(NoTransactionFoundException.class);
    }

    @Test
    void whenDeletingOtherUserTransaction_thenItIsNotRemovedFromDB() {
        String otherUserId = UUID.randomUUID().toString();
        Instant transactionTime = Instant.now();
        BigDecimal amount = BigDecimal.valueOf(100L);
        Transaction transaction = createAndSaveTransaction(transactionTime, amount, otherUserId);

        assertThat(transactionRepository
                .findTransactionById(transaction.getId(), otherUserId))
                .isPresent()
                .get().extracting(Transaction::getId).isNotNull();

        transactionRepository.deleteByIdAndUserId(transaction.getId(), currentLoggedInUserId);

        assertThat(transactionRepository.findTransactionById(transaction.getId(), otherUserId))
                .isPresent()
                .get().extracting(Transaction::getId).isEqualTo(transaction.getId());
    }

    Transaction createAndSaveTransaction(Instant transactionTime, BigDecimal amount, String userId) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setUserId(userId);
        transaction.setSubCategory(createAndSaveSubCategory(userId));
        transaction.setTransactionDateTime(transactionTime);
        transaction.setNote("NOTE");
        return transactionRepository.saveAndFlush(transaction);
    }

    SubCategory createAndSaveSubCategory(String userId) {
        SubCategory subCategory = new SubCategory();
        subCategory.setName("CustomSubCategory");
        subCategory.setUserId(userId);
        subCategory.setSystem(false);
        subCategory.setCategory(createAndSaveCategory(userId));
        return subCategoryRepository.saveAndFlush(subCategory);
    }

    Category createAndSaveCategory(String userId) {
        Category category = new Category();
        category.setUserId(userId);
        category.setSystem(false);
        category.setName("CustomCategory");
        return categoryRepository.saveAndFlush(category);
    }

}
