package com.cashigo.expensio.common.util;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.SubCategory;

import java.util.HashMap;
import java.util.List;

public class CategoryUtil {

    private static final ThreadLocal<HashMap<String, HashMap<String, SubCategory>>> categoryCache = new ThreadLocal<>();

    public static Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setUserId(UserContext.getUserId());
        category.setSystem(false);
        return category;
    }

    public static void createCategoryCache(List<Category> categories) {
        HashMap<String, HashMap<String, SubCategory>> cache = new HashMap<>();
        for (Category category : categories) {
            String categoryName = category.getName().toLowerCase();
            HashMap<String, SubCategory> subCategoryCache = new HashMap<>();
            cache.put(categoryName, subCategoryCache);
            for (SubCategory subCategory : category.getSubCategories())
                subCategoryCache.put(subCategory.getName().toLowerCase(), subCategory);
        }
        categoryCache.set(cache);
    }

    public static SubCategory getSubCategory(String categoryName, String subCategoryName) {
        HashMap<String, HashMap<String, SubCategory>> cache = categoryCache.get();
        if (cache.containsKey(categoryName.toLowerCase())) {
            HashMap<String, SubCategory> subCategoryCache = cache.get(categoryName.toLowerCase());
            if (subCategoryCache.containsKey(subCategoryName.toLowerCase()))
                return subCategoryCache.get(subCategoryName.toLowerCase());
        }
        return null;
    }

    public static void clearCache() {
        categoryCache.remove();
    }

}
