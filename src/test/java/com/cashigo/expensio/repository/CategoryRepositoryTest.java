package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private static String userId;

    @BeforeAll
    static void init() {
        userId = UUID.randomUUID().toString();
    }

    @Test
    void userShouldHaveSystemCategories_whenNoCustomCategoriesCreated() {
        List<Category> categories = categoryRepository.findCategoriesOfUserWithSubCategories(userId, Sort.by("name"));
        Assertions.assertThat(categories)
                .hasSize(7)
                .allMatch(Category::isSystem)
                .allMatch(category -> category.getUserId() == null);
    }

}
