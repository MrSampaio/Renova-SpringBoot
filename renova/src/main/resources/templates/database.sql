-- =====================================================================
-- RENOVA - database script (English, UUID keys)
--
-- WARNING: this script DROPS the existing "renova" database and creates
-- it again from scratch. Export any data you want to keep before running.
--
-- Naming rule: every column is the snake_case version of the Java field
-- (userId -> user_id, bookingDate -> booking_date...), so Hibernate maps
-- the entities with no @Column(name = ...) needed.
-- =====================================================================

DROP DATABASE IF EXISTS renova;

CREATE DATABASE renova;

USE renova;

-- ---------------------------------------------------------------------
-- TABLES
-- Primary keys are UUID strings (36 chars). When an INSERT does not
-- provide the id, MySQL generates it with UUID().
-- ---------------------------------------------------------------------

CREATE TABLE users (
                       user_id VARCHAR(36) NOT NULL DEFAULT (UUID()),
                       name VARCHAR(100) NOT NULL,
                       phone CHAR(11),
                       email VARCHAR(100) UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       user_role ENUM('CUSTOMER', 'EMPLOYEE') DEFAULT 'CUSTOMER' NOT NULL,
                       PRIMARY KEY (user_id)
);

CREATE TABLE products (
                          product_id VARCHAR(36) NOT NULL DEFAULT (UUID()),
                          product_name VARCHAR(100) NOT NULL,
                          description VARCHAR(150) NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          status ENUM('AVAILABLE', 'RESERVED', 'SOLD') DEFAULT 'AVAILABLE',
                          PRIMARY KEY (product_id)
);

CREATE TABLE bookings (
                          booking_id VARCHAR(36) NOT NULL DEFAULT (UUID()),
                          user_id VARCHAR(36) NOT NULL,
                          total_amount DECIMAL(10, 2) NOT NULL,
                          payment_method ENUM('CASH', 'DEBIT', 'PIX') DEFAULT 'DEBIT',
                          booking_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                          expiration_date DATE,
                          status ENUM('AWAITING_PAYMENT', 'COMPLETED', 'CANCELLED') DEFAULT 'AWAITING_PAYMENT',
                          PRIMARY KEY (booking_id),
                          FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE booking_items (
                               booking_id VARCHAR(36) NOT NULL,
                               product_id VARCHAR(36) NOT NULL UNIQUE,
                               PRIMARY KEY (booking_id, product_id),
                               FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
                               FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- ---------------------------------------------------------------------
-- USER PROCEDURES
-- ---------------------------------------------------------------------

DELIMITER //

CREATE PROCEDURE PROC_LOGIN_USER(
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(255)
)
BEGIN
SELECT
    user_id,
    name,
    user_role
FROM
    users
WHERE
    email = p_email
  AND password = p_password;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_REGISTER_USER(
    IN p_name VARCHAR(100),
    IN p_phone VARCHAR(20),
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(255),
    IN p_user_role VARCHAR(100)
)
BEGIN
INSERT INTO users (name, phone, email, password, user_role)
VALUES (p_name, p_phone, p_email, p_password, p_user_role);
END //

DELIMITER ;

-- ---------------------------------------------------------------------
-- PRODUCT PROCEDURES
-- ---------------------------------------------------------------------

DELIMITER //

CREATE PROCEDURE PROC_REGISTER_PRODUCT(
    IN p_product_name VARCHAR(100),
    IN p_description VARCHAR(150),
    IN p_price DECIMAL(10, 2),
    IN p_status VARCHAR(20)
)
BEGIN
INSERT INTO products (product_name, description, price, status)
VALUES (p_product_name, p_description, p_price, p_status);
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_LIST_PRODUCTS()
BEGIN
SELECT
    product_id,
    product_name,
    description,
    price,
    status
FROM
    products;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_FIND_PRODUCT_BY_ID(
    IN p_product_id VARCHAR(36)
)
BEGIN
SELECT
    product_id,
    product_name,
    description,
    price,
    status
FROM
    products
WHERE
    product_id = p_product_id;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_FIND_PRODUCTS_BY_NAME(
    IN p_name VARCHAR(100)
)
BEGIN
SELECT
    product_id,
    product_name,
    description,
    price,
    status
FROM
    products
WHERE
    product_name LIKE CONCAT('%', p_name, '%');
END //

DELIMITER ;

-- ---------------------------------------------------------------------
-- BOOKING PROCEDURES
-- ---------------------------------------------------------------------

DELIMITER //

CREATE PROCEDURE PROC_CREATE_BOOKING(
    IN p_user_id VARCHAR(36),
    IN p_total DECIMAL(10, 2),
    IN p_payment_method VARCHAR(50),
    OUT p_generated_id VARCHAR(36)
)
BEGIN
    SET p_generated_id = UUID();

INSERT INTO bookings (booking_id, user_id, total_amount, payment_method, expiration_date)
VALUES (p_generated_id, p_user_id, p_total, p_payment_method, DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY));
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_INSERT_BOOKING_ITEM(
    IN p_booking_id VARCHAR(36),
    IN p_product_id VARCHAR(36)
)
BEGIN
INSERT INTO booking_items (booking_id, product_id)
VALUES (p_booking_id, p_product_id);

UPDATE products
SET status = 'RESERVED'
WHERE product_id = p_product_id;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_LIST_BOOKINGS_BY_USER(
    IN p_user_id VARCHAR(36)
)
BEGIN
SELECT
    booking_id,
    total_amount,
    payment_method,
    booking_date,
    expiration_date,
    status
FROM
    bookings
WHERE
    user_id = p_user_id;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_LIST_PRODUCTS_BY_BOOKING(
    IN p_booking_id VARCHAR(36)
)
BEGIN
SELECT
    bi.booking_id,
    p.product_id,
    p.product_name,
    p.description,
    p.price
FROM
    booking_items bi
        INNER JOIN
    products p ON bi.product_id = p.product_id
WHERE
    bi.booking_id = p_booking_id;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_FIND_BOOKING_BY_ID(
    IN p_booking_id VARCHAR(36),
    IN p_user_id VARCHAR(36)
)
BEGIN
SELECT
    booking_id,
    total_amount,
    payment_method,
    booking_date,
    expiration_date,
    status
FROM
    bookings
WHERE
    booking_id = p_booking_id
  AND user_id = p_user_id;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_LIST_ALL_BOOKINGS()
BEGIN
SELECT
    b.booking_id,
    u.name AS customer_name,
    b.total_amount,
    b.payment_method,
    b.booking_date,
    b.expiration_date,
    b.status
FROM
    bookings b
        INNER JOIN
    users u ON b.user_id = u.user_id
ORDER BY
    b.booking_date DESC;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_FIND_ANY_BOOKING_BY_ID(
    IN p_booking_id VARCHAR(36)
)
BEGIN
SELECT
    b.booking_id,
    u.name AS customer_name,
    b.total_amount,
    b.payment_method,
    b.booking_date,
    b.expiration_date,
    b.status
FROM
    bookings b
        INNER JOIN
    users u ON b.user_id = u.user_id
WHERE
    b.booking_id = p_booking_id;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_FIND_BOOKINGS_BY_CUSTOMER_NAME(
    IN p_customer_name VARCHAR(100)
)
BEGIN
SELECT
    b.booking_id,
    u.name AS customer_name,
    b.total_amount,
    b.payment_method,
    b.booking_date,
    b.expiration_date,
    b.status
FROM
    bookings b
        INNER JOIN
    users u ON b.user_id = u.user_id
WHERE
    u.name LIKE CONCAT('%', p_customer_name, '%')
ORDER BY
    b.booking_date DESC;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE PROC_UPDATE_BOOKING_STATUS(
    IN p_booking_id VARCHAR(36),
    IN p_new_status VARCHAR(50)
)
BEGIN
    -- 1. Updates the status of the booking itself
UPDATE bookings
SET status = p_new_status
WHERE booking_id = p_booking_id;

-- 2. If the booking is CANCELLED, its products become AVAILABLE again
IF p_new_status = 'CANCELLED' THEN
UPDATE products p
    INNER JOIN booking_items bi ON p.product_id = bi.product_id
    SET p.status = 'AVAILABLE'
WHERE bi.booking_id = p_booking_id;
END IF;

    -- 3. If the booking is COMPLETED, its products are permanently SOLD
    IF p_new_status = 'COMPLETED' THEN
UPDATE products p
    INNER JOIN booking_items bi ON p.product_id = bi.product_id
    SET p.status = 'SOLD'
WHERE bi.booking_id = p_booking_id;
END IF;
END //

DELIMITER ;

-- ---------------------------------------------------------------------
-- SEED DATA
-- ---------------------------------------------------------------------

INSERT INTO products (product_name, description, price, status) VALUES
                                                                    ('White Dress Shirt', 'Long-sleeve 100% cotton shirt', 89.90, 'AVAILABLE'),
                                                                    ('Men''s Jeans', 'Blue jeans size 40', 120.50, 'AVAILABLE'),
                                                                    ('Running Shoes', 'Black sports shoes size 42', 199.99, 'SOLD'),
                                                                    ('Floral Dress', 'Summer dress size M', 75.00, 'RESERVED'),
                                                                    ('Leather Jacket', 'Black faux leather jacket', 250.00, 'AVAILABLE'),
                                                                    ('Basic Black T-Shirt', 'Cotton crew-neck t-shirt', 35.90, 'AVAILABLE'),
                                                                    ('Pleated Skirt', 'Pink pleated midi skirt', 65.00, 'SOLD'),
                                                                    ('Winter Sweater', 'Red knitted wool sweater', 110.00, 'RESERVED'),
                                                                    ('Denim Shorts', 'Women''s denim shorts size 38', 55.00, 'AVAILABLE'),
                                                                    ('Leather Belt', 'Men''s brown belt with silver buckle', 45.00, 'AVAILABLE'),
                                                                    ('Crossbody Bag', 'Small black bag with chain strap', 90.00, 'SOLD'),
                                                                    ('Sunglasses', 'Aviator-style sunglasses with dark lenses', 130.00, 'AVAILABLE'),
                                                                    ('Winter Boots', 'Women''s black ankle boots', 180.00, 'AVAILABLE'),
                                                                    ('Blue Polo Shirt', 'Men''s polo shirt size L', 70.00, 'RESERVED'),
                                                                    ('Sweatpants', 'Gray unisex sweatpants', 85.50, 'AVAILABLE'),
                                                                    ('Beach Hat', 'Wide-brim straw hat', 40.00, 'SOLD'),
                                                                    ('Sock Set', 'Set of 3 pairs of white sports socks', 25.00, 'AVAILABLE'),
                                                                    ('Analog Watch', 'Water-resistant silver wristwatch', 150.00, 'RESERVED'),
                                                                    ('Plaid Scarf', 'Soft wool scarf for winter', 45.90, 'AVAILABLE'),
                                                                    ('School Backpack', 'Black backpack with multiple compartments', 110.00, 'AVAILABLE');

INSERT INTO products (product_name, description, price, status) VALUES
                                                                    ('Silk Blouse', 'Women''s pure silk short-sleeve blouse', 145.00, 'AVAILABLE'),
                                                                    ('Twill Bermuda Shorts', 'Men''s beige twill bermuda shorts', 89.90, 'AVAILABLE'),
                                                                    ('Wool Beanie', 'Unisex wool beanie for extreme cold', 35.00, 'SOLD'),
                                                                    ('Pencil Skirt', 'Black formal pencil skirt', 79.90, 'RESERVED'),
                                                                    ('Plaid Shirt', 'Red and black plaid flannel shirt', 115.00, 'AVAILABLE'),
                                                                    ('Women''s Flats', 'Pointed-toe faux leather flats', 65.50, 'AVAILABLE'),
                                                                    ('Winter Coat', 'Long waterproof lined coat', 320.00, 'SOLD'),
                                                                    ('Printed Swimsuit', 'Beach swimsuit with tropical print', 95.00, 'AVAILABLE'),
                                                                    ('Black Swim Trunks', 'Basic black men''s swim trunks', 45.00, 'RESERVED'),
                                                                    ('Women''s Belt', 'Thin red belt with gold buckle', 30.00, 'AVAILABLE'),
                                                                    ('Dress Shoes', 'Men''s brown leather shoes', 185.00, 'AVAILABLE'),
                                                                    ('Evening Gown', 'Long navy blue gala dress', 450.00, 'SOLD'),
                                                                    ('Men''s Blazer', 'Slim fit gray blazer, Italian cut', 280.00, 'AVAILABLE'),
                                                                    ('Tights', 'Skin-tone tights, 40 denier', 20.00, 'RESERVED'),
                                                                    ('Leather Wallet', 'Slim men''s wallet with coin pocket', 55.00, 'AVAILABLE'),
                                                                    ('High-Waist Bikini', 'Retro-style bikini set', 85.00, 'SOLD'),
                                                                    ('Windbreaker', 'Lightweight waterproof sports jacket', 135.90, 'AVAILABLE'),
                                                                    ('Cotton Pajamas', 'Men''s long pajama set', 90.00, 'AVAILABLE'),
                                                                    ('Flat-Brim Cap', 'Urban-style adjustable cap', 40.00, 'RESERVED'),
                                                                    ('Leather Gloves', 'Lined leather winter gloves', 110.00, 'AVAILABLE');

INSERT INTO users (name, phone, email, password, user_role)
VALUES ('Default Employee', '11999999999', 'funcionario1@gmail.com', '12345', 'EMPLOYEE');

-- ---------------------------------------------------------------------
-- QUICK CHECKS
-- ---------------------------------------------------------------------

SELECT * FROM users;
SELECT * FROM products;
SELECT * FROM bookings;
SELECT * FROM booking_items;