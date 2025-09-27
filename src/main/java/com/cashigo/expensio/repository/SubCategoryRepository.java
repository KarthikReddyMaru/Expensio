package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    @Query("select s from SubCategory s where s.id = :subCategoryId and (s.userId = :userId or s.isSystem = true)")
    Optional<SubCategory> findSubCategoryById(Long subCategoryId, String userId);

    @Query("""
        select case when count(sc) > 0 then true else false end
            from SubCategory sc where sc.id = :subCategoryId and (sc.userId = :userId or sc.isSystem = true)
    """)
    boolean existsSubCategoriesById(Long subCategoryId, String userId);

    void deleteSubCategoryByIdAndCategory_UserId(Long subCategoryId, String categoryUserId);

}
