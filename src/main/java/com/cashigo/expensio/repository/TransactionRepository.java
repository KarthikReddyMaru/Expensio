package com.cashigo.expensio.repository;

import com.cashigo.expensio.dto.ReportProjection;
import com.cashigo.expensio.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("select t from Transaction t where t.id = :id and t.userId = :userId")
    Optional<Transaction> findTransactionById(UUID id, String userId);

    @Query("""
        select t from Transaction t
            left join fetch t.subCategory sc
            left join fetch sc.category
        where t.userId = :userId and t.id = :id
    """)
    Optional<Transaction> findTransactionByIdWithSubCat(UUID id, String userId);

    @Query("""
        select t from Transaction t
        where t.userId = :userId
                    and t.subCategory.id in :subCategoryIds
                    and t.transactionDateTime between :start and :end
    """)
    List<Transaction> findTransactionsByInstantRangeWithSubCategories(
            String userId, List<Long> subCategoryIds, Instant start, Instant end);

    @Query(
            value = "select distinct t from Transaction t left join fetch t.subCategory sc left join fetch sc.category where t.userId = :userId",
            countQuery = "select count(t) from Transaction t where t.userId = :userId"
    )
    Page<Transaction> findTransactionsOfUserWithSubCategories(String userId, Pageable pageable);

    @Query(value = """
        SELECT c.name as category, COALESCE(SUM(t.amount), 0) as amountSpent from category c
        left join sub_category sc
        	on c.id = sc.category_id and (sc.is_system = true or sc.user_id = ?3)
        left join transaction t
        	on sc.id = t.sub_category_id and t.user_id = ?3 and t.transaction_date_time BETWEEN ?1 and ?2
        group by c.id
        UNION all
        select 'Anonymous', COALESCE(SUM(t2.amount),0) from transaction t2
        where t2.user_id = ?3 and t2.sub_category_id is null
    """, nativeQuery = true)
    List<ReportProjection> findTransactionReportByInstantRange(Instant start, Instant end, String userId);

    boolean existsByIdAndUserId(UUID id, String userId);

    void deleteByIdAndUserId(UUID id, String userId);
    
}
