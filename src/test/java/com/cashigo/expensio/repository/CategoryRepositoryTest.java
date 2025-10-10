package com.cashigo.expensio.repository;

import com.cashigo.expensio.config.RepositoryConfig;
import com.cashigo.expensio.dto.exception.NoCategoryFoundException;
import com.cashigo.expensio.model.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(RepositoryConfig.class)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private static String userId;
    private static Sort sortByName;
    private static Long systemCategoryId;

    @BeforeAll
    static void init() {
        userId = UUID.randomUUID().toString();
        sortByName = Sort.by("name");
        systemCategoryId = 1L;
    }

    @Test
    void whenFetchingUserCategoriesWithNoCustomCategories_thenSystemCategoriesReturned() {
        List<Category> categories = categoryRepository.findCategoriesOfUserWithSubCategories(userId, sortByName);
        assertThat(categories)
                .isNotEmpty()
                .allMatch(Category::isSystem)
                .extracting(Category::getUserId)
                .allSatisfy(userId -> assertThat(userId).isNull());
    }

    @Test
    void whenFetchingUserCategoriesWithCustomCategories_thenSystemCategoriesAndCustomCategoriesReturned() {
        List<Category> newCategories = List.of(createCustomCategory("Education"), createCustomCategory("Health"));
        categoryRepository.saveAll(newCategories);
        List<Category> categories = categoryRepository.findCategoriesOfUserWithSubCategories(userId, sortByName);

        assertThat(categories)
                .isNotEmpty();
        assertThat(categories)
                .extracting(Category::getUserId)
                .filteredOn(Objects::nonNull)
                .hasSize(2);
    }

    @Test
    void whenFetchingUserCategories_thenOtherUserCategoriesAreNotReturned() {
        Category categoryOfUser1 = createCustomCategory("CategoryOfUser1");
        categoryOfUser1.setUserId(UUID.randomUUID().toString());
        Category categoryOfUser2 = createCustomCategory("CategoryOfUser2");
        categoryOfUser2.setUserId(UUID.randomUUID().toString());
        categoryRepository.saveAll(List.of(categoryOfUser1, categoryOfUser2));

        List<Category> categories = categoryRepository.findCategoriesOfUserWithSubCategories(userId, sortByName);
        assertThat(categories)
                .isNotEmpty();
        assertThat(categories)
                .extracting(Category::getUserId)
                .filteredOn(Objects::nonNull)
                .hasSize(0);
    }

    @Test
    void whenFetchingOtherUserCategoryById_thenExceptionThrown() {
        Category categoryOfUser1 = createCustomCategory("CategoryOfUser1");
        categoryOfUser1.setUserId(UUID.randomUUID().toString());
        Category savedCategory = categoryRepository.save(categoryOfUser1);
        Long categoryId = savedCategory.getId();

        assertThatThrownBy(() ->
                categoryRepository.findCategoryById(categoryId, userId)
                        .orElseThrow(NoCategoryFoundException::new)
        ).isInstanceOf(NoCategoryFoundException.class);
    }

    @Test
    void whenFetchingSystemCategoryById_thenItIsReturnedCorrectly() {
        Category category = categoryRepository.findCategoryByIdWithSubCategories(systemCategoryId, userId).orElseThrow();
        assertThat(category.getName()).isEqualTo("Food & Dining");
        assertThat(category.getUserId()).isNull();
        assertThat(category.getSubCategories()).isNotEmpty();
        assertThat(category.getCreatedAt()).isNotNull();
    }

    @Test
    void whenFetchingCustomCategory_thenItIsReturnedCorrectly() {
        Category category = createCustomCategory("Education");
        Category savedCategory = categoryRepository.save(category);
        Long categoryId = savedCategory.getId();
        Category educationCategory = categoryRepository.findCategoryById(categoryId, userId).orElseThrow();
        assertThat(educationCategory.getName()).isEqualTo("Education");
        assertThat(educationCategory.getUserId()).isEqualTo(userId);
        assertThat(educationCategory.getCreatedAt()).isNotNull();
    }

    @Test
    void whenFetchingInvalidCategoryId_thenExceptionIsThrown() {
        assertThatThrownBy(() ->
                categoryRepository.findCategoryById(999L, userId)
                        .orElseThrow(NoCategoryFoundException::new)
        ).isInstanceOf(NoCategoryFoundException.class);
    }

    @Test
    void whenSavingCustomCategory_thenItIsPersistedCorrectly() {
        Category category = createCustomCategory("Custom Category");

        Category savedCategory = categoryRepository.save(category);

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Custom Category");
        assertThat(savedCategory.getUserId()).isEqualTo(userId);
        assertThat(savedCategory.getCreatedAt()).isNotNull();
    }

    @Test
    void whenSystemCategoryExistsById_thenTrueReturned() {
        boolean found = categoryRepository.existsCategoryById(systemCategoryId, userId);
        assertThat(found).isTrue();
    }

    @Test
    void whenCustomCategoryExistsById_thenTrueReturned() {
        Category category = createCustomCategory("Education");
        Category savedCategory = categoryRepository.save(category);
        Long savedCategoryId = savedCategory.getId();
        boolean found = categoryRepository.existsCategoryById(savedCategoryId, userId);
        assertThat(found).isTrue();
    }

    @Test
    void whenCategoryIdNotExists_thenFalseReturned() {
        boolean found = categoryRepository.existsCategoryById(999L, userId);
        assertThat(found).isFalse();
    }

    @Test
    void whenOtherUserCategoryExistsButNotCurrentLoggedInUserCategory_thenFalseReturned() {
        Category categoryOfUser1 = createCustomCategory("CategoryOfUser1");
        categoryOfUser1.setUserId(UUID.randomUUID().toString());
        Category savedCategory = categoryRepository.save(categoryOfUser1);
        Long categoryId = savedCategory.getId();
        boolean found = categoryRepository.existsCategoryById(categoryId, userId);
        assertThat(found).isFalse();
    }

    @Test
    void whenDeletingExistingCategory_thenCategoryDeletedSuccessfully() {
        Category unsavedCategory = createCustomCategory("Education");
        Category savedCategory = categoryRepository.save(unsavedCategory);
        assertThat(savedCategory).satisfies((category) -> {
            assertThat(category.getName()).isEqualTo("Education");
            assertThat(category.getUserId()).isEqualTo(userId);
            assertThat(category.getId()).isNotNull();
        });
        categoryRepository.deleteCategoryByIdAndUserId(savedCategory.getId(), userId);
        assertThat(
                categoryRepository.existsCategoryById(savedCategory.getId(), userId)
        ).isFalse();
    }

    @Test
    void whenDeletingOtherUserCategory_thenCategoryShouldNotBeDeleted() {
        String otherUserId = UUID.randomUUID().toString();
        Category category = createCustomCategory("CategoryOfOtherUser");
        category.setUserId(otherUserId);
        Category savedCategory = categoryRepository.save(category);
        Long savedCategoryId = savedCategory.getId();

        Category fetchSavedCategory = categoryRepository.findCategoryById(savedCategoryId, otherUserId).orElseThrow();

        assertThat(fetchSavedCategory)
                .satisfies((fetchedCategory) -> {
                    assertThat(fetchedCategory.getId()).isEqualTo(savedCategoryId);
                    assertThat(fetchedCategory.getName()).isEqualTo("CategoryOfOtherUser");
                    assertThat(fetchedCategory.getUserId()).isEqualTo(otherUserId);
                });

        categoryRepository.deleteCategoryByIdAndUserId(savedCategoryId, userId);

        fetchSavedCategory = categoryRepository.findCategoryById(savedCategoryId, otherUserId).orElseThrow();

        assertThat(fetchSavedCategory)
                .satisfies((fetchedCategory) -> {
                    assertThat(fetchedCategory.getId()).isEqualTo(savedCategoryId);
                    assertThat(fetchedCategory.getName()).isEqualTo("CategoryOfOtherUser");
                    assertThat(fetchedCategory.getUserId()).isEqualTo(otherUserId);
                });
    }

    Category createCustomCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setUserId(userId);
        category.setSystem(false);
        return category;
    }
}
