package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.common.util.CategoryUtil;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.repository.CategoryRepository;
import com.cashigo.expensio.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryImportService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createNonExistedCategories(HashMap<String, List<String>> categories) {

        List<Category> existedCategories = categoryRepository
                .findCategoriesOfUserWithSubCategories(UserContext.getUserId(), Sort.by("name"));

        CategoryUtil.createCategoryCache(existedCategories);

        List<SubCategory> nonExistedSubCategories = CategoryUtil.createNonExistedSubCategories(categories);
        subCategoryRepository.saveAllAndFlush(nonExistedSubCategories);

        CategoryUtil.clearCache();
    }

}
