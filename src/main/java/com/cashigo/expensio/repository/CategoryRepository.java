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

    @Query("select distinct c from Category c left join fetch c.subCategories where c.userId = :userId or c.isSystem = true ")
    List<Category> findCategoriesByUserId(@Param("userId") String userId, Sort sort);

    @Query("select c from Category c left join fetch c.subCategories where c.id = :id and (c.userId = :userId or c.isSystem = true )")
    Optional<Category> findCategoryByIdWithSubCategories(Long id, String userId);

    boolean existsCategoryByIdAndUserId(Long id, String userId);

    void deleteCategoryByIdAndUserId(Long id, String userId);
}
