CREATE TABLE product_category (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE product (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INT,
    product_category_id INT,
    image_url VARCHAR(255),
    stock INT,
    delete_flg INT DEFAULT 0,
    CONSTRAINT fk_product_category FOREIGN KEY (product_category_id) REFERENCES product_category(id)
);
