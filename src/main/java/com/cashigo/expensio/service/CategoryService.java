package com.cashigo.expensio.service;

import com.cashigo.expensio.dto.CategoryDto;
import com.cashigo.expensio.dto.exception.CategoryNotFoundException;
import com.cashigo.expensio.dto.mapper.CategoryMapper;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(categoryMapper::mapToDto).toList();
    }

    @SneakyThrows
    public CategoryDto getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        Category data = category.orElseThrow(CategoryNotFoundException::new);
        return categoryMapper.mapToDto(data);
    }

    public CategoryDto saveAndUpdateCategory(CategoryDto unsavedCategory) {
        log.info("Unsaved category {}", unsavedCategory);
        Category newCategory = categoryMapper.mapToEntity(unsavedCategory);
        newCategory.setUserId("SomeIdForNow");
        Category savedCategory = categoryRepository.save(newCategory);
        log.info("Saved category {}", savedCategory);
        return categoryMapper.mapToDto(savedCategory);
    }

    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
        log.info("Category with id {} is deleted", id);
    }
}
