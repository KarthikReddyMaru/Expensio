package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    @Query("select s from SubCategory s where s.category.id = :categoryId and (s.category.userId = :userId or s.isSystem = true)")
    List<SubCategory> findSubCategoriesByCategoryIdAndUserIdOrSystem(Long categoryId, String userId);

    @Query("select s from SubCategory s where s.id = :id and (s.category.userId = :userId or s.isSystem = true)")
    Optional<SubCategory> findSubCategoryByIdAndUserIdOrSystem(Long id, String userId);

    void deleteSubCategoryByIdAndCategory_UserId(Long id, String categoryUserId);

}
