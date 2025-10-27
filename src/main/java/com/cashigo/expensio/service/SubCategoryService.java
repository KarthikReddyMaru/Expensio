package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.dto.exception.NoCategoryFoundException;
import com.cashigo.expensio.dto.exception.NoSubCategoryFoundException;
import com.cashigo.expensio.dto.exception.SystemPropertiesCannotBeModifiedException;
import com.cashigo.expensio.dto.mapper.SubCategoryMapper;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.repository.CategoryRepository;
import com.cashigo.expensio.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubCategoryService {

    @Setter
    @Value("${system.sub.categories}")
    private Long systemSubCategories;

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryMapper subCategoryMapper;

    @SneakyThrows
    public List<SubCategoryDto> getSubCategories(Long categoryId) {
        Category category = categoryRepository
                .findCategoryByIdWithSubCategories(categoryId, UserContext.getUserId())
                .orElseThrow(NoCategoryFoundException::new);
        List<SubCategory> subCategories = category.getSubCategories();
        return subCategories.stream().map(subCategoryMapper::mapToDto).toList();
    }

    @SneakyThrows
    public List<SubCategory> getSubCategoryEntities(Long categoryId) {
        Category category = categoryRepository
                .findCategoryByIdWithSubCategories(categoryId, UserContext.getUserId())
                .orElseThrow(NoCategoryFoundException::new);
        return category.getSubCategories();
    }

    @SneakyThrows
    public SubCategoryDto getSubCategoryById(Long subCategoryId) {
        Optional<SubCategory> subCategory = subCategoryRepository.findSubCategoryById(subCategoryId, UserContext.getUserId());
        SubCategory data = subCategory.orElseThrow(NoSubCategoryFoundException::new);
        return subCategoryMapper.mapToDto(data);
    }

    @Transactional
    @SneakyThrows
    public SubCategoryDto saveSubCategory(SubCategoryDto unsavedSubCategory) {
        checkCategoryModificationScope(unsavedSubCategory);
        checkSubCategoryModificationScope(unsavedSubCategory);

        SubCategory subCategory = subCategoryMapper.mapToEntity(unsavedSubCategory);
        subCategory.setUserId(UserContext.getUserId());
        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        return subCategoryMapper.mapToDto(savedSubCategory);
    }

    private void checkCategoryModificationScope(SubCategoryDto unsavedSubCategory) throws NoCategoryFoundException {
        Long categoryId = unsavedSubCategory.getCategoryId();
        boolean categoryExists = categoryRepository.existsCategoryById(categoryId, UserContext.getUserId());
        if (!categoryExists)
            throw new NoCategoryFoundException();
    }

    private void checkSubCategoryModificationScope(SubCategoryDto unsavedSubCategory) throws NoSubCategoryFoundException {
        Long subCategoryId = unsavedSubCategory.getId();
        if (subCategoryId == null) return;
        if (subCategoryId <= systemSubCategories)
            throw new SystemPropertiesCannotBeModifiedException();
        if (!subCategoryRepository.existsSubCategoriesById(subCategoryId, UserContext.getUserId()))
            throw new NoSubCategoryFoundException();
    }

    @Transactional
    public void deleteSubCategory(Long subCategoryId) {
        if (subCategoryId <= systemSubCategories)
            throw new SystemPropertiesCannotBeModifiedException();
        subCategoryRepository.deleteSubCategoryByIdAndUserId(subCategoryId, UserContext.getUserId());
    }

}
