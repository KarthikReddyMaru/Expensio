package com.cashigo.expensio.repository;

import com.cashigo.expensio.config.RepositoryConfig;
import com.cashigo.expensio.dto.exception.NoSubCategoryFoundException;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.SubCategory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@DataJpaTest
@Import(RepositoryConfig.class)
public class SubCategoryRepositoryTest {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String userId;
    private Long systemSubCategoryId;
    private Long systemCategoryIds;

    @BeforeEach
    void init() {
        userId = UUID.randomUUID().toString();
        systemSubCategoryId = new Random().nextLong(32) + 1; // 32 system SubCategories
        systemCategoryIds = 7L; // 7 system Categories
    }

    @Test
    void whenFetchingSystemSubCategoryById_thenItIsReturnedSuccessfully() {
        log.info("SubCatId {}", systemSubCategoryId);
        SubCategory systemSubCategory = subCategoryRepository.findSubCategoryById(systemSubCategoryId, userId).orElseThrow();
        assertThat(systemSubCategory)
                .satisfies(subCategory -> {
                    assertThat(subCategory.getId()).as("SubCategory ID").isEqualTo(systemSubCategoryId);
                    assertThat(subCategory.getUserId()).as("User Id").isNull();
                    assertThat(subCategory.getCategory().getId()).as("Category Id").isBetween(1L, systemCategoryIds);
                });
    }

    @Test
    void whenFetchingInvalidSubCategoryById_thenExceptionIsThrown() {
        assertThatThrownBy(() ->
                subCategoryRepository.findSubCategoryById(999L, userId)
                        .orElseThrow(NoSubCategoryFoundException::new)
        )
                .as("Invalid Fetching SubCategory with Invalid Id")
                .isInstanceOf(NoSubCategoryFoundException.class);
    }

    @Test
    void whenSavingCustomSubCategoryUnderCustomCategory_thenItIsPersistedSuccessfully() {
        SubCategory newSubCategory = createSubCategory("CustomSubCategory", userId);
        SubCategory savedSubCategory = subCategoryRepository.save(newSubCategory);
        SubCategory fetchedSubCategory = subCategoryRepository
                .findSubCategoryById(savedSubCategory.getId(), userId)
                .orElseThrow();

        assertThat(fetchedSubCategory).satisfies(subCategory -> {

            assertThat(subCategory.getName())
                    .as("SubCategory name")
                    .isEqualTo("CustomSubCategory");

            assertThat(subCategory.getId())
                    .as("Sub Category ID")
                    .isNotNull();

            assertThat(subCategory.getCreatedAt())
                    .as("Created at")
                    .isNotNull();

            assertThat(subCategory.getUserId())
                    .as("User Id")
                    .isEqualTo(userId);
        });
    }

    @Test
    void whenSavingCustomSubCategoryUnderSystemCategory_thenItIsPersistedSuccessfully() {
        SubCategory unsavedSubCategory = createSubCategoryUnderSystemCategory();
        SubCategory savedSubCategory = subCategoryRepository.save(unsavedSubCategory);
        SubCategory fetchedSubCategory = subCategoryRepository
                .findSubCategoryById(savedSubCategory.getId(), userId).orElseThrow();

        assertThat(fetchedSubCategory).satisfies(subCategory -> {
            assertThat(subCategory.getUserId()).as("UserId").isEqualTo(userId);
            assertThat(subCategory.getCategory().getId()).isBetween(1L, systemCategoryIds);
            assertThat(subCategory.getName()).isEqualTo("SubCategoryUnderSystemCategory");
        });
    }

    @Test
    void whenFetchingOtherUserCreatedSubCategoryById_thenExceptionIsThrown() {
        String otherUserId = UUID.randomUUID().toString();
        SubCategory subCategoryOfOtherUser = createSubCategory("SubCategoryOfOtherUser", otherUserId);
        SubCategory savedSubCategoryOfOtherUser = subCategoryRepository.save(subCategoryOfOtherUser);

        SubCategory fetchSubCategoryOfOtherUser = subCategoryRepository
                .findSubCategoryById(savedSubCategoryOfOtherUser.getId(), otherUserId)
                .orElseThrow();

        assertThat(fetchSubCategoryOfOtherUser.getName()).isEqualTo("SubCategoryOfOtherUser");

        assertThatThrownBy(() ->
                subCategoryRepository.findSubCategoryById(savedSubCategoryOfOtherUser.getId(), userId)
                        .orElseThrow(NoSubCategoryFoundException::new)
        )
                .as("Other user created sub category Id")
                .isInstanceOf(NoSubCategoryFoundException.class);
    }

    @Test
    void whenSystemSubCategoryExists_thenTrueReturned() {
        boolean found = subCategoryRepository.existsSubCategoriesById(systemSubCategoryId, userId);
        assertThat(found).isTrue();
    }

    @Test
    void whenCustomSubCategoryExists_thenTrueReturned() {
        SubCategory subCategory = createSubCategory("SubCategory", userId);
        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        boolean found = subCategoryRepository.existsSubCategoriesById(savedSubCategory.getId(), userId);
        assertThat(found).isTrue();
    }

    @Test
    void whenCustomSubCategoryOfOtherUserExists_thenFalseReturned() {
        String otherUserId = UUID.randomUUID().toString();
        SubCategory subCategory = createSubCategory("SubCategoryOfOtherUser", otherUserId);
        SubCategory savedSubCategoryOfOtherUser = subCategoryRepository.save(subCategory);
        boolean fetchedByCurrentLoggedInUser = subCategoryRepository
                .existsSubCategoriesById(savedSubCategoryOfOtherUser.getId(), userId);
        assertThat(fetchedByCurrentLoggedInUser).isFalse();
    }

    @Test
    void whenDeletingCustomSubCategory_thenCustomSubCategoryIsDeleted() {
        SubCategory subCategory = createSubCategory("SubCategory", userId);
        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        SubCategory fetchSavedSubcategory = subCategoryRepository
                .findSubCategoryById(savedSubCategory.getId(), userId).orElseThrow();

        assertThat(fetchSavedSubcategory.getName()).isEqualTo("SubCategory");
        assertThat(fetchSavedSubcategory.getUserId()).isEqualTo(userId);

        subCategoryRepository.deleteSubCategoryByIdAndUserId(fetchSavedSubcategory.getId(), userId);
        subCategoryRepository.flush();

        boolean found = subCategoryRepository.existsSubCategoriesById(fetchSavedSubcategory.getId(), userId);

        assertThat(found).isFalse();
    }

    @Test
    void whenDeletingOtherUserSubCategoryById_thenSubCategoryIsNotDeleted() {
        String otherUserId = UUID.randomUUID().toString();
        SubCategory subCategoryOfOtherUser = createSubCategory("SubCategoryOfOtherUser", otherUserId);
        SubCategory savedSubCategoryOfOtherUser = subCategoryRepository.save(subCategoryOfOtherUser);
        SubCategory fetchSavedSubCategoryOfOtherUser = subCategoryRepository
                .findSubCategoryById(savedSubCategoryOfOtherUser.getId(), otherUserId).orElseThrow();

        assertThat(fetchSavedSubCategoryOfOtherUser.getName()).isEqualTo("SubCategoryOfOtherUser");

        subCategoryRepository.deleteSubCategoryByIdAndUserId(fetchSavedSubCategoryOfOtherUser.getId(), userId);
        subCategoryRepository.flush();

        fetchSavedSubCategoryOfOtherUser = subCategoryRepository
                .findSubCategoryById(savedSubCategoryOfOtherUser.getId(), otherUserId).orElseThrow();
        assertThat(fetchSavedSubCategoryOfOtherUser.getName()).isEqualTo("SubCategoryOfOtherUser");

    }

    SubCategory createSubCategory(String name, String userId) {
        SubCategory subCategory = new SubCategory();
        subCategory.setName(name);
        subCategory.setUserId(userId);
        subCategory.setSystem(false);
        subCategory.setCategory(createAndSaveCategory(userId));
        return subCategory;
    }

    Category createAndSaveCategory(String userId) {
        Category category = new Category();
        category.setUserId(userId);
        category.setSystem(false);
        category.setName("CustomCategory");
        return categoryRepository.save(category);
    }

    SubCategory createSubCategoryUnderSystemCategory() {
        Category category = new Category();
        category.setId(new Random().nextLong(systemCategoryIds) + 1);
        SubCategory subCategory = new SubCategory();
        subCategory.setCategory(category);
        subCategory.setName("SubCategoryUnderSystemCategory");
        subCategory.setUserId(userId);
        return subCategory;
    }

}
