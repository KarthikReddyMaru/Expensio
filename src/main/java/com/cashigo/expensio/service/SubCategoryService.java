package com.cashigo.expensio.service;

import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.dto.mapper.SubCategoryMapper;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final SubCategoryMapper subCategoryMapper;

    public List<SubCategoryDto> getSubCategories(Long categoryId) {
        List<SubCategory> subCategories = subCategoryRepository.findSubCategoriesByCategory_Id(categoryId);
        return subCategories.stream().map(subCategoryMapper::mapToDto).toList();
    }

    public SubCategoryDto saveSubCategory(SubCategoryDto unsavedSubCategory) {
        SubCategory subCategory = subCategoryMapper.mapToEntity(unsavedSubCategory);
        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        log.info("Sub category is saved/updated in category (Id: {})", savedSubCategory.getCategory().getId());
        return subCategoryMapper.mapToDto(savedSubCategory);
    }

    public void deleteSubCategory(Long subCategoryId) {
        subCategoryRepository.deleteById(subCategoryId);
        log.info("Sub Category with id {} is deleted", subCategoryId);
    }

}
