INSERT INTO product_category (id, name) VALUES (1, '文具');
INSERT INTO product_category (id, name) VALUES (2, '文房具');

INSERT INTO product (name, price, product_category_id, image_url, stock, delete_flg) VALUES ('ペン', 150, 1, 'pen.png', 100, 0);
INSERT INTO product (name, price, product_category_id, image_url, stock, delete_flg) VALUES ('ノート', 300, 1, 'note.png', 50, 0);
INSERT INTO product (name, price, product_category_id, image_url, stock, delete_flg) VALUES ('消しゴム', 80, 2, 'eraser.png', 20, 1);

