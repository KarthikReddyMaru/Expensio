package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.CategoryDto;
import com.cashigo.expensio.dto.exception.SystemPropertiesCannotBeModifiedException;
import com.cashigo.expensio.dto.mapper.CategoryMapper;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.repository.CategoryRepository;
import com.cashigo.expensio.service.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void init() {
        String currentLoggedInUserId = UUID.randomUUID().toString();
        try(MockedStatic<UserContext> mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getUserId).thenReturn(currentLoggedInUserId);
        }
        categoryService.setSystemCategories(7L);
    }

    @Test
    void whenSavingCategory_thenItIsPersisted() {
        CategoryDto unsavedCategoryDto = createCategory(null);
        Category unsavedCategory = new Category();
        Category savedCategory = new Category();
        CategoryDto savedCategoryDto = new CategoryDto();

        when(categoryMapper.mapToEntity(unsavedCategoryDto)).thenReturn(unsavedCategory);
        when(categoryRepository.save(argThat(category ->
            category.getUserId().equals(UserContext.getUserId())
        ))).thenReturn(savedCategory);
        when(categoryMapper.mapToDto(savedCategory)).thenReturn(savedCategoryDto);

        CategoryDto actual = categoryService.saveCategory(unsavedCategoryDto);
        assertThat(actual).isEqualTo(savedCategoryDto);
    }

    @Test
    void whenUpdatingCategory_thenItIsPersisted() {
        Long categoryId = 8L;
        CategoryDto unsavedCategoryDto = createCategory(categoryId);
        Category savedCategory = new Category();
        savedCategory.setName("Previous Category");
        Category updatedCategory = new Category();
        CategoryDto savedCategoryDto = new CategoryDto();

        when(categoryRepository.findCategoryById(categoryId, UserContext.getUserId()))
                .thenReturn(Optional.of(savedCategory));
        when(categoryRepository.save(argThat(category ->
                category.getName().equals("Custom Category")
        ))).thenReturn(updatedCategory);
        when(categoryMapper.mapToDto(updatedCategory)).thenReturn(savedCategoryDto);

        CategoryDto actual = categoryService.updateCategory(unsavedCategoryDto);
        assertThat(actual).isEqualTo(savedCategoryDto);
    }

    @Test
    void whenUpdatingSystemCategory_thenExceptionIsThrown() {
        Long categoryId = 3L;
        CategoryDto unsavedCategoryDto = createCategory(categoryId);
        assertThatThrownBy(() -> categoryService.updateCategory(unsavedCategoryDto))
                .isInstanceOf(SystemPropertiesCannotBeModifiedException.class);
    }

    @Test
    void whenDeletingSystemCategory_thenExceptionIsThrown() {
        Long categoryId = 3L;
        assertThatThrownBy(() -> categoryService.deleteCategoryById(categoryId))
                .isInstanceOf(SystemPropertiesCannotBeModifiedException.class);
    }

    @Test
    void whenDeletingCustomCategory_thenItIsRemovedFromDB() {
        Long categoryId = 20L;
        categoryService.deleteCategoryById(categoryId);
        verify(categoryRepository).deleteCategoryByIdAndUserId(categoryId, UserContext.getUserId());
    }

    private CategoryDto createCategory(Long id) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Custom Category");
        categoryDto.setId(id);
        return categoryDto;
    }
}
