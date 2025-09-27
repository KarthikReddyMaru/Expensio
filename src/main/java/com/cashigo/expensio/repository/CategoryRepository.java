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

    @Query("""
        select distinct c from Category c
            left join fetch c.subCategories sc
        where c.userId = :userId or (c.isSystem = true and (sc.isSystem = true or sc.userId = :userId))
    """)
    List<Category> findCategoriesOfUserWithSubCategories(@Param("userId") String userId, Sort sort);

    @Query("select c from Category c where c.id = :categoryId and c.userId = :userId")
    Optional<Category> findCategoryById(Long categoryId, String userId);

    @Query("""
        select c from Category c
            left join fetch c.subCategories sc
        where c.id = :categoryId and (c.userId = :userId or (c.isSystem = true and (sc.isSystem = true or sc.userId = :userId)))
    """)
    Optional<Category> findCategoryByIdWithSubCategories(Long categoryId, String userId);

    @Query("""
        select case when count(c) > 0 then true else false end
        from Category c where c.id = :id and (c.userId = :userId or c.isSystem = true )
    """)
    boolean existsCategoryById(Long id, String userId);

    void deleteCategoryByIdAndUserId(Long id, String userId);
}
