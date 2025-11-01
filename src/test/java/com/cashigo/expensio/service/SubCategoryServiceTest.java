package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.dto.exception.NoCategoryFoundException;
import com.cashigo.expensio.dto.exception.NoSubCategoryFoundException;
import com.cashigo.expensio.dto.exception.SystemPropertiesCannotBeModifiedException;
import com.cashigo.expensio.dto.mapper.SubCategoryMapper;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.repository.CategoryRepository;
import com.cashigo.expensio.repository.SubCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubCategoryServiceTest {

    @Mock
    private SubCategoryRepository subCategoryRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SubCategoryMapper subCategoryMapper;

    @InjectMocks
    private SubCategoryService subCategoryService;

    @BeforeEach
    public void init() {
        String currentLoggedInUserId = UUID.randomUUID().toString();
        try(MockedStatic<UserContext> mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getUserId).thenReturn(currentLoggedInUserId);
        }
        subCategoryService.setSystemSubCategories(32L);
    }


    @Test
    void whenSavingNewSubCategory_thenItIsPersisted() {
        Long categoryId = 8L;
        SubCategoryDto unsavedSubCategoryDto = new SubCategoryDto();
        unsavedSubCategoryDto.setCategoryId(categoryId);
        SubCategory savedSubCategory = new SubCategory();
        SubCategoryDto savedSubCategoryDto = new SubCategoryDto();

        when(subCategoryMapper.mapToEntity(unsavedSubCategoryDto)).thenCallRealMethod();
        when(categoryRepository.existsCategoryById(categoryId, UserContext.getUserId())).thenReturn(true);
        when(subCategoryRepository.save(argThat(unsavedSubCategory ->
            unsavedSubCategory.getId() == null &&
            unsavedSubCategory.getCategory() != null &&
            unsavedSubCategory.getCategory().getId().equals(categoryId) &&
            !unsavedSubCategory.isSystem()
        ))).thenReturn(savedSubCategory);
        when(subCategoryMapper.mapToDto(savedSubCategory)).thenReturn(savedSubCategoryDto);

        SubCategoryDto actualDto = subCategoryService.saveSubCategory(unsavedSubCategoryDto);

        assertThat(actualDto).isEqualTo(savedSubCategoryDto);
        verify(categoryRepository).existsCategoryById(categoryId, UserContext.getUserId());
        verify(subCategoryRepository, never()).existsSubCategoriesById(anyLong(), eq(UserContext.getUserId()));
    }

    @Test
    void whenSavingNewSubCategoryUnderOtherUserCategoryId_thenExceptionIsThrown() {
        Long categoryId = 8L;
        SubCategoryDto unsavedSubCategoryDto = new SubCategoryDto();
        unsavedSubCategoryDto.setCategoryId(categoryId);

        when(categoryRepository.existsCategoryById(categoryId, UserContext.getUserId())).thenReturn(false);

        assertThatThrownBy(() ->
                subCategoryService.saveSubCategory(unsavedSubCategoryDto)
        ).isInstanceOf(NoCategoryFoundException.class);

        verify(categoryRepository).existsCategoryById(categoryId, UserContext.getUserId());
        verify(subCategoryRepository, never()).existsSubCategoriesById(anyLong(), eq(UserContext.getUserId()));
    }

    @Test
    void whenUpdatingSubCategory_thenItIsPersisted() {
        Long categoryId = 8L, subCategoryId = 34L;
        SubCategoryDto unsavedSubCategoryDto = new SubCategoryDto();
        unsavedSubCategoryDto.setName("Updated Name");
        unsavedSubCategoryDto.setId(subCategoryId);
        unsavedSubCategoryDto.setCategoryId(categoryId);
        SubCategory savedSubCategory = new SubCategory();
        SubCategoryDto savedSubCategoryDto = new SubCategoryDto();

        when(subCategoryMapper.mapToEntity(unsavedSubCategoryDto)).thenCallRealMethod();
        when(categoryRepository.existsCategoryById(categoryId, UserContext.getUserId())).thenReturn(true);
        when(subCategoryRepository.existsSubCategoriesById(subCategoryId, UserContext.getUserId())).thenReturn(true);
        when(subCategoryRepository.save(argThat(unsavedSubCategory ->
                unsavedSubCategory.getId().equals(subCategoryId) &&
                unsavedSubCategory.getName().equals("Updated Name") &&
                unsavedSubCategory.getUserId().equals(UserContext.getUserId()) &&
                unsavedSubCategory.getCategory() != null &&
                unsavedSubCategory.getCategory().getId().equals(categoryId) &&
                !unsavedSubCategory.isSystem()
        ))).thenReturn(savedSubCategory);
        when(subCategoryMapper.mapToDto(savedSubCategory)).thenReturn(savedSubCategoryDto);

        SubCategoryDto actualDto = subCategoryService.saveSubCategory(unsavedSubCategoryDto);

        assertThat(actualDto).isEqualTo(savedSubCategoryDto);
        verify(categoryRepository).existsCategoryById(categoryId, UserContext.getUserId());
        verify(subCategoryRepository).existsSubCategoriesById(subCategoryId, UserContext.getUserId());
    }

    @Test
    void whenUpdatingSystemSubCategory_thenExceptionIsThrown() {
        Long categoryId = 3L, subCategoryId = 32L;
        SubCategoryDto unsavedSubCategoryDto = new SubCategoryDto();
        unsavedSubCategoryDto.setId(subCategoryId);
        unsavedSubCategoryDto.setCategoryId(categoryId);
        when(categoryRepository.existsCategoryById(categoryId, UserContext.getUserId()))
                .thenReturn(true);

        assertThatThrownBy(
                () -> subCategoryService.saveSubCategory(unsavedSubCategoryDto)
        ).isInstanceOf(SystemPropertiesCannotBeModifiedException.class);

        verify(subCategoryRepository, never()).save(any(SubCategory.class));
    }

    @Test
    void whenUpdatingOtherUserSystemSubCategory_thenExceptionIsThrown() {
        Long categoryId = 3L, subCategoryId = 34L;
        SubCategoryDto unsavedSubCategoryDto = new SubCategoryDto();
        unsavedSubCategoryDto.setId(subCategoryId);
        unsavedSubCategoryDto.setCategoryId(categoryId);
        when(categoryRepository.existsCategoryById(categoryId, UserContext.getUserId()))
                .thenReturn(true);
        when(subCategoryRepository.existsSubCategoriesById(subCategoryId, UserContext.getUserId()))
                .thenReturn(false);

        assertThatThrownBy(
                () -> subCategoryService.saveSubCategory(unsavedSubCategoryDto)
        ).isInstanceOf(NoSubCategoryFoundException.class);

        verify(subCategoryRepository, never()).save(any(SubCategory.class));
    }

    @Test
    void whenDeletingSystemSubCategory_thenExceptionIsThrown() {
        Long subCategoryId = 32L;

        assertThatThrownBy(
                () -> subCategoryService.deleteSubCategory(subCategoryId)
        ).isInstanceOf(SystemPropertiesCannotBeModifiedException.class);

        verify(subCategoryRepository, never()).deleteSubCategoryByIdAndUserId(subCategoryId, UserContext.getUserId());
    }

}
