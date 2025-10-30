package com.cashigo.expensio.common.util;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.SubCategory;

import java.util.*;

public class CategoryUtil {

    private static final ThreadLocal<HashMap<String, Category>> categoryCache = new ThreadLocal<>();
    private static final ThreadLocal<HashMap<String, HashMap<String, SubCategory>>> subCategoryCache = new ThreadLocal<>();

    public static Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setUserId(UserContext.getUserId());
        category.setSystem(false);
        return category;
    }

    public static SubCategory createSubCategory(Category category, String subCategoryName) {
        SubCategory subCategory = new SubCategory();
        subCategory.setName(subCategoryName);
        subCategory.setCategory(category);
        subCategory.setUserId(UserContext.getUserId());
        subCategory.setSystem(false);
        return subCategory;
    }

    public static List<SubCategory> createNonExistedSubCategories(Map<String, List<String>> categories) {

        List<SubCategory> subCategories = new ArrayList<>();

        HashMap<String, Category> categoryMap = new HashMap<>();
        HashMap<String, HashSet<String>> subCategoryMap = new HashMap<>();

        HashMap<String, Category> categoryCacheMap = categoryCache.get();

        for (Map.Entry<String, List<String>> entry: categories.entrySet()) {

            String categoryName = entry.getKey().toLowerCase();
            if (categoryCacheMap.containsKey(categoryName)) {
                categoryMap.put(categoryName, categoryCacheMap.get(categoryName));
            } else {
                categoryMap.put(categoryName, CategoryUtil.createCategory(entry.getKey()));
            }

            addExistingSubCategories(subCategoryMap, categoryName);
            Category currentCategory = categoryMap.get(categoryName);

            for (String subCategory : entry.getValue()) {
                if (!subCategoryMap.get(categoryName).contains(subCategory.toLowerCase()))
                    subCategories.add(CategoryUtil.createSubCategory(currentCategory, subCategory));
            }
        }

        return subCategories;
    }

    private static void addExistingSubCategories(HashMap<String, HashSet<String>> subCategoryMap, String categoryName) {

        HashSet<String> subCategories = new HashSet<>();
        HashMap<String, HashMap<String, SubCategory>> subCategoryCacheData = subCategoryCache.get();

        if (subCategoryCacheData.containsKey(categoryName))
            subCategories = new HashSet<>(subCategoryCacheData.get(categoryName).keySet());

        subCategoryMap.put(categoryName, subCategories);
    }

    public static void createCategoryCache(List<Category> categories) {

        HashMap<String, Category> categoryCacheData = new HashMap<>();
        HashMap<String, HashMap<String, SubCategory>> subCategoryCacheMap = new HashMap<>();

        for (Category category : categories) {
            String categoryName = category.getName().toLowerCase();

            HashMap<String, SubCategory> subCategoryCacheData = new HashMap<>();
            subCategoryCacheMap.put(categoryName, subCategoryCacheData);

            categoryCacheData.put(categoryName, category);

            for (SubCategory subCategory : category.getSubCategories())
                subCategoryCacheData.put(subCategory.getName().toLowerCase(), subCategory);
        }

        categoryCache.set(categoryCacheData);
        subCategoryCache.set(subCategoryCacheMap);
    }

    public static Category getCategory(String categoryName) {
        HashMap<String, Category> categoryCacheData = categoryCache.get();
        if (categoryCacheData.containsKey(categoryName.toLowerCase())) {
            return categoryCacheData.get(categoryName.toLowerCase());
        }
        return null;
    }

    public static SubCategory getSubCategory(String categoryName, String subCategoryName) {
        HashMap<String, HashMap<String, SubCategory>> subCategoryCacheMap = subCategoryCache.get();
        if (subCategoryCacheMap.containsKey(categoryName.toLowerCase())) {
            HashMap<String, SubCategory> subCategoryCacheData = subCategoryCacheMap.get(categoryName.toLowerCase());
            if (subCategoryCacheData.containsKey(subCategoryName.toLowerCase()))
                return subCategoryCacheData.get(subCategoryName.toLowerCase());
        }
        return null;
    }

    public static void clearCache() {
        categoryCache.remove();
        subCategoryCache.remove();
    }

}
