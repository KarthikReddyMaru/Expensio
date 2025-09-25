package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.dto.exception.NoSubCategoryFoundException;
import com.cashigo.expensio.dto.mapper.SubCategoryMapper;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final SubCategoryMapper subCategoryMapper;
    private final UserContext userContext;

    public List<SubCategoryDto> getSubCategories(Long categoryId) {
        String userId = userContext.getUserId();
        List<SubCategory> subCategories = subCategoryRepository.findSubCategoriesByCategoryIdAndUserIdOrSystem(categoryId, userId);
        return subCategories.stream().map(subCategoryMapper::mapToDto).toList();
    }

    public List<SubCategory> getSubCategoryEntities(Long categoryId) {
        String userId = userContext.getUserId();
        return subCategoryRepository.findSubCategoriesByCategoryIdAndUserIdOrSystem(categoryId, userId);
    }

    @SneakyThrows
    public SubCategoryDto getSubCategoryById(Long subCategoryId) {
        String userId = userContext.getUserId();
        Optional<SubCategory> subCategory = subCategoryRepository.findSubCategoryByIdAndUserIdOrSystem(subCategoryId, userId);
        SubCategory data = subCategory.orElseThrow(NoSubCategoryFoundException::new);
        return subCategoryMapper.mapToDto(data);
    }

    @Transactional
    public SubCategoryDto saveSubCategory(SubCategoryDto unsavedSubCategory) {
        SubCategory subCategory = subCategoryMapper.mapToEntity(unsavedSubCategory);
        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        log.info("Sub category of {} is saved/updated in category (Id: {})",
                userContext.getUserName(), savedSubCategory.getCategory().getId());
        return subCategoryMapper.mapToDto(savedSubCategory);
    }

    @Transactional
    public void deleteSubCategory(Long subCategoryId) {
        String userId = userContext.getUserId();
        subCategoryRepository.deleteSubCategoryByIdAndCategory_UserId(subCategoryId, userId);
        log.info("Sub Category of {} with id {} is deleted",
                userContext.getUserName(), subCategoryId);
    }

}
