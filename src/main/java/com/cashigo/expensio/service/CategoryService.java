package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.CategoryDto;
import com.cashigo.expensio.dto.exception.CategoryNotFoundException;
import com.cashigo.expensio.dto.mapper.CategoryMapper;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserContext userContext;

    public List<CategoryDto> getAllCategoriesByUserId() {
        String userId = userContext.getUserId().orElse("Anonymous");
        Sort sort = Sort.by("name").ascending();
        List<Category> categories = categoryRepository.findCategoriesByUserIdOrSystem(userId, sort);
        return categories.stream().map(categoryMapper::mapToDto).toList();
    }

    @SneakyThrows
    public CategoryDto getCategoryById(Long id) {
        String userId = userContext.getUserId().orElse("Anonymous");
        Optional<Category> category = categoryRepository.findCategoryByIdAndUserIdOrSystem(id, userId);
        Category data = category.orElseThrow(CategoryNotFoundException::new);
        return categoryMapper.mapToDto(data);
    }

    @SneakyThrows
    public CategoryDto saveAndUpdateCategory(CategoryDto unsavedCategory) {
        log.info("Unsaved category {}", unsavedCategory);
        Category newCategory = categoryMapper.mapToEntity(unsavedCategory);
        String userId = userContext.getUserId().orElse("Anonymous");
        newCategory.setUserId(userId);
        Category savedCategory = categoryRepository.save(newCategory);
        log.info("Saved category {}", savedCategory);
        return categoryMapper.mapToDto(savedCategory);
    }

    public void deleteCategoryById(Long id) {
        String userId = userContext.getUserId().orElse("Anonymous");
        categoryRepository.deleteCategoryByIdAndUserId(id, userId);
        log.info("Category with id {} is deleted", id);
    }
}
