package com.cashigo.expensio.common.util;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.SubCategory;


public class SubCategoryUtil {

    public static SubCategory createSubCategory(Category category, String name) {
        SubCategory subCategory = new SubCategory();
        subCategory.setName(name);
        subCategory.setCategory(category);
        subCategory.setUserId(UserContext.getUserId());
        subCategory.setSystem(false);
        return subCategory;
    }

}
