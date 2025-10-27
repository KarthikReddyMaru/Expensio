package com.cashigo.expensio.common.util;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.model.Category;

public class CategoryUtil {

    public static Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setUserId(UserContext.getUserId());
        category.setSystem(false);
        return category;
    }

}
