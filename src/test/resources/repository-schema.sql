DROP TABLE IF EXISTS order_detail CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS payment_method CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS order_status CASCADE;

DROP TABLE IF EXISTS product_stock CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS product_category CASCADE;

DROP TABLE IF EXISTS employee_account CASCADE;
DROP TABLE IF EXISTS employee CASCADE;
DROP TABLE IF EXISTS department CASCADE;

CREATE TABLE product_category (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE product (
    id serial PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INT,
    product_category_id INT,
    image_url VARCHAR(255),
    stock INT,
    delete_flg INT DEFAULT 0,
    CONSTRAINT fk_product_category FOREIGN KEY (product_category_id) REFERENCES product_category(id)
);

CREATE TABLE product_stock(
    id serial PRIMARY KEY,
    product_id INT,
    quantity INT,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES product(id)

)