package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findTransactionByIdAndUserId(UUID id, String userId);

    List<Transaction> findTransactionsByUserId(String userId, Pageable pageable);

    void deleteByIdAndUserId(UUID id, String userId);
    
}
