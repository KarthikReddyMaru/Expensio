package com.cashigo.expensio.config;

import com.cashigo.expensio.model.Category;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.model.Transaction;
import com.cashigo.expensio.repository.CategoryRepository;
import com.cashigo.expensio.repository.TransactionRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
public class BootConfig {

    private SubCategory createSubCategory(String name, Category category) {
        SubCategory sub = new SubCategory();
        sub.setName(name);
        sub.setSystem(true);
        sub.setCategory(category);
        return sub;
    }

    private SubCategory createSubCategory(Category category) {
        SubCategory sub = new SubCategory();
        sub.setName("Something");
        sub.setSystem(false);
        sub.setCategory(category);
        return sub;
    }

    private SubCategory createSubCategoryRef(long id) {
        SubCategory sub = new SubCategory();
        sub.setId(id); // only set ID
        return sub;
    }

    @Bean
    ApplicationRunner applicationRunner(CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        return args -> {

            // 1. Food & Dining
            Category food = new Category();
            food.setName("Food & Dining");
            food.setSystem(true);
            food.setUserId(null);

            food.setSubCategories(
                    java.util.List.of(
                            createSubCategory("Breakfast", food),
                            createSubCategory("Lunch", food),
                            createSubCategory("Dinner", food),
                            createSubCategory("Restaurant", food),
                            createSubCategory("Snacks & Beverages", food)
                    )
            );
            categoryRepository.save(food);

            // 2. Social Life
            Category social = new Category();
            social.setName("Social Life");
            social.setSystem(true);
            social.setUserId(null);

            social.setSubCategories(
                    java.util.List.of(
                            createSubCategory("Friends", social),
                            createSubCategory("Alumni", social),
                            createSubCategory("Party/Events", social)
                    )
            );
            categoryRepository.save(social);

            // 3. Gift
            Category gift = new Category();
            gift.setName("Gift");
            gift.setSystem(true);
            gift.setUserId(null);

            gift.setSubCategories(
                    java.util.List.of(
                            createSubCategory("Jewellery", gift),
                            createSubCategory("Toys", gift),
                            createSubCategory("Gift Card", gift),
                            createSubCategory("Flowers", gift)
                    )
            );
            categoryRepository.save(gift);

            // 4. Transport
            Category transport = new Category();
            transport.setName("Transport");
            transport.setSystem(true);
            transport.setUserId(null);

            transport.setSubCategories(
                    java.util.List.of(
                            createSubCategory("Bus", transport),
                            createSubCategory("Subway", transport),
                            createSubCategory("Metro", transport),
                            createSubCategory("Cab", transport),
                            createSubCategory("Fuel", transport),
                            createSubCategory("Parking", transport)
                    )
            );
            categoryRepository.save(transport);

            // 5. Culture
            Category culture = new Category();
            culture.setName("Culture");
            culture.setSystem(true);
            culture.setUserId(null);

            culture.setSubCategories(
                    java.util.List.of(
                            createSubCategory("Books", culture),
                            createSubCategory("Movie", culture),
                            createSubCategory("Music", culture),
                            createSubCategory("Apps", culture),
                            createSubCategory("Events", culture)
                    )
            );
            categoryRepository.save(culture);

            // 6. Household
            Category household = new Category();
            household.setName("Household");
            household.setSystem(true);
            household.setUserId(null);

            household.setSubCategories(
                    java.util.List.of(
                            createSubCategory("Kitchen", household),
                            createSubCategory("Furniture", household),
                            createSubCategory("Appliances", household),
                            createSubCategory("Toiletries", household),
                            createSubCategory("Cleaning", household)
                    )
            );
            categoryRepository.save(household);

            // 7. Apparel
            Category apparel = new Category();
            apparel.setName("Apparel");
            apparel.setSystem(true);
            apparel.setUserId(null);

            apparel.setSubCategories(
                    java.util.List.of(
                            createSubCategory("Clothing", apparel),
                            createSubCategory("Shoes", apparel),
                            createSubCategory("Laundry", apparel),
                            createSubCategory("Accessories", apparel)
                    )
            );

            categoryRepository.save(apparel);

            // 8. Custom
            Category others = new Category();
            others.setName("Others");
            others.setSystem(false);
            others.setUserId("Anonymous");

            others.setSubCategories(
                    java.util.List.of(
                            createSubCategory(others)
                    )
            );

            categoryRepository.save(others);

            Transaction t1 = new Transaction();
            t1.setUserId("226bc242-1f1d-4ad7-b480-3e2bb0c94f16");
            t1.setAmount(BigDecimal.valueOf(150.0));
            t1.setSubCategory(createSubCategoryRef(3));
            t1.setTransactionDateTime(Instant.now().minus(3, ChronoUnit.DAYS));

            Transaction t2 = new Transaction();
            t2.setUserId("226bc242-1f1d-4ad7-b480-3e2bb0c94f16");
            t2.setAmount(BigDecimal.valueOf(300.0));
            t2.setSubCategory(createSubCategoryRef(21));
            t2.setTransactionDateTime(Instant.now().minus(2, ChronoUnit.DAYS));

            Transaction t3 = new Transaction();
            t3.setUserId("226bc242-1f1d-4ad7-b480-3e2bb0c94f16");
            t3.setAmount(BigDecimal.valueOf(450.0));
            t3.setSubCategory(createSubCategoryRef(13));
            t3.setTransactionDateTime(Instant.now().minus(1, ChronoUnit.DAYS));

            transactionRepository.saveAll(List.of(t1, t2, t3));

        };
    }

}
