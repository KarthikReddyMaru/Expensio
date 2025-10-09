package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.CategoryDto;
import com.cashigo.expensio.dto.exception.NoCategoryFoundException;
import com.cashigo.expensio.dto.exception.SystemPropertiesCannotBeModifiedException;
import com.cashigo.expensio.dto.mapper.CategoryMapper;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserContext userContext;

    @Setter
    @Value("${system.categories}")
    private Long systemCategories;

    public List<CategoryDto> getAllCategoriesByUserId() {
        String userId = userContext.getUserId();
        Sort sort = Sort.by("name").ascending();
        List<Category> categories = categoryRepository.findCategoriesOfUserWithSubCategories(userId, sort);
        return categories.stream().map(categoryMapper::mapToDto).toList();
    }

    @SneakyThrows
    public CategoryDto getCategoryById(Long id) {
        String userId = userContext.getUserId();
        Optional<Category> category = categoryRepository.findCategoryByIdWithSubCategories(id, userId);
        Category data = category.orElseThrow(NoCategoryFoundException::new);
        return categoryMapper.mapToDto(data);
    }

    @SneakyThrows
    @Transactional
    public CategoryDto saveCategory(CategoryDto unsavedCategory) {
        Long categoryId = unsavedCategory.getId();
        if (categoryId != null && categoryId <= systemCategories)
            throw new SystemPropertiesCannotBeModifiedException();
        Category newCategory = categoryMapper.mapToEntity(unsavedCategory);
        String userId = userContext.getUserId();
        newCategory.setUserId(userId);
        Category savedCategory = categoryRepository.save(newCategory);
        return categoryMapper.mapToDto(savedCategory);
    }

    @SneakyThrows
    @Transactional
    public CategoryDto updateCategory(CategoryDto category) {
        Long categoryId = category.getId();
        if (categoryId != null && categoryId <= systemCategories)
            throw new SystemPropertiesCannotBeModifiedException();
        String userId = userContext.getUserId();
        Category savedCategory = categoryRepository
                .findCategoryById(categoryId, userId)
                .orElseThrow(NoCategoryFoundException::new);
        savedCategory.setName(category.getName());
        Category updatedCategory = categoryRepository.save(savedCategory);
        return categoryMapper.mapToDto(updatedCategory);
    }

    @Transactional
    public void deleteCategoryById(Long categoryId) {
        if (categoryId <= systemCategories)
            throw new SystemPropertiesCannotBeModifiedException();
        String userId = userContext.getUserId();
        categoryRepository.deleteCategoryByIdAndUserId(categoryId, userId);
    }
}
