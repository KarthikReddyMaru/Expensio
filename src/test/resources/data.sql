INSERT INTO category (is_system, created_at, name, user_id)
VALUES (1, NOW(), 'Food & Dining', NULL),
       (1, NOW(), 'Social Life', NULL),
       (1, NOW(), 'Gift', NULL),
       (1, NOW(), 'Transport', NULL),
       (1, NOW(), 'Culture', NULL),
       (1, NOW(), 'Household', NULL),
       (1, NOW(), 'Apparel', NULL);


INSERT INTO sub_category (is_system, category_id, created_at, name, user_id)
VALUES (1, 1, NOW(), 'Breakfast', NULL),
       (1, 1, NOW(), 'Lunch', NULL),
       (1, 1, NOW(), 'Dinner', NULL),
       (1, 1, NOW(), 'Restaurant', NULL),
       (1, 1, NOW(), 'Snacks & Beverages', NULL),
       (1, 2, NOW(), 'Friends', NULL),
       (1, 2, NOW(), 'Alumni', NULL),
       (1, 2, NOW(), 'Party/Events', NULL),
       (1, 3, NOW(), 'Jewellery', NULL),
       (1, 3, NOW(), 'Toys', NULL);
INSERT INTO sub_category (is_system, category_id, created_at, name, user_id)
VALUES (1, 3, NOW(), 'Gift Card', NULL),
       (1, 3, NOW(), 'Flowers', NULL),
       (1, 4, NOW(), 'Bus', NULL),
       (1, 4, NOW(), 'Subway', NULL),
       (1, 4, NOW(), 'Metro', NULL),
       (1, 4, NOW(), 'Cab', NULL),
       (1, 4, NOW(), 'Fuel', NULL),
       (1, 4, NOW(), 'Parking', NULL),
       (1, 5, NOW(), 'Books', NULL),
       (1, 5, NOW(), 'Movie', NULL);
INSERT INTO sub_category (is_system, category_id, created_at, name, user_id)
VALUES (1, 5, NOW(), 'Music', NULL),
       (1, 5, NOW(), 'Apps', NULL),
       (1, 5, NOW(), 'Events', NULL),
       (1, 6, NOW(), 'Kitchen', NULL),
       (1, 6, NOW(), 'Furniture', NULL),
       (1, 6, NOW(), 'Appliances', NULL),
       (1, 6, NOW(), 'Toiletries', NULL),
       (1, 6, NOW(), 'Cleaning', NULL),
       (1, 7, NOW(), 'Clothing', NULL),
       (1, 7, NOW(), 'Shoes', NULL);
INSERT INTO sub_category (is_system, category_id, created_at, name, user_id)
VALUES (1, 7, NOW(), 'Laundry', NULL),
       (1, 7, NOW(), 'Accessories', NULL);
