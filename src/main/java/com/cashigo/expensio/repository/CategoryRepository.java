package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c where c.userId = :userId or c.isSystem = true ")
    List<Category> findCategoriesByUserIdOrSystem(@Param("userId") String userId, Sort sort);

    @Query("select c from Category c where c.id = :id and (c.userId = :userId or c.isSystem = true )")
    Optional<Category> findCategoryByIdAndUserIdOrSystem(Long id, String userId);

    void deleteCategoryByIdAndUserId(Long id, String userId);
}
