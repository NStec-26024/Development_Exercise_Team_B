-- department
INSERT INTO department (name)
VALUES
    ('人事部'),
    ('企画部'),
    ('システム開発部');

-- employee
INSERT INTO employee (department_id, name, name_kana)
SELECT id, '山田太郎', 'ヤマダタロウ'
FROM department
WHERE name = '人事部';

INSERT INTO employee (department_id, name, name_kana)
SELECT id, '川田次郎', 'カワタジロウ'
FROM department
WHERE name = '企画部';

INSERT INTO employee (department_id, name, name_kana)
SELECT id, '海田三郎', 'ウミタサブロウ'
FROM department
WHERE name = 'システム開発部';

-- employee_account
INSERT INTO employee_account (employee_id, name, password)
SELECT id, 'yamadatarou1001', 'yamadapassword1001'
FROM employee
WHERE name = '山田太郎';

-- product_category
INSERT INTO product_category (name)
VALUES
    ('文具'),
    ('雑貨'),
    ('パソコン周辺機器');

-- product
INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'マーカー(青)', 110, 'blue_maker.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '油性ボールペン(青)', 111, 'blue_pen_o.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '水性ボールペン(青)', 112, 'blue_pen_w.jpeg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '鉛筆', 113, 'black_pen.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '油性ボールペン(黒)', 114, 'black_pen_o.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '水性ボールペン(黒)', 115, 'black_pen_w.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '筆ペン(黒)', 116, 'black_fudepen.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '鉛筆(赤)', 117, 'red_pen.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'マーカー(ピンク)', 118, 'red_maker.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '油性ボールペン(赤)', 119, 'red_pen_o.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '水性ボールペン(赤)', 120, 'red_pen_w.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '筆ペン(赤)', 121, 'red_fudepen.jpeg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'マーカー(黄)', 122, 'yellow_maker.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'マーカー(緑)', 123, 'green_maker.jpg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '色鉛筆セット(12色)', 124, 'color_pen12.jpeg', 0
FROM product_category WHERE name = '文具';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '色鉛筆セット(24色)', 125, 'color_pen48.jpeg', 0
FROM product_category WHERE name = '文具';


-- 雑貨
INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'トートバッグ', 1500, 'bag.jpg', 0
FROM product_category WHERE name = '雑貨';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'ハンカチ', 800, 'cloth.jpg', 0
FROM product_category WHERE name = '雑貨';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'ストラップ付キーホルダー(二重リング)', 700, 'keyholder.jpg', 0
FROM product_category WHERE name = '雑貨';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'ワンタッチキーホルダー', 1000, 'keyholder2.jpeg', 0
FROM product_category WHERE name = '雑貨';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'アイマスク', 1300, 'mask.jpg', 0
FROM product_category WHERE name = '雑貨';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '防水スプレー', 1200, 'spray.jpg', 0
FROM product_category WHERE name = '雑貨';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '折りたたみ傘', 2000, 'umbrella.jpg', 0
FROM product_category WHERE name = '雑貨';


-- パソコン周辺機器
INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'ワイヤレスマウス', 900, 'mouse_c.jpg', 0
FROM product_category WHERE name = 'パソコン周辺機器';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, 'ワイヤレストラックボール', 1300, 'mouse_d.jpeg', 0
FROM product_category WHERE name = 'パソコン周辺機器';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '有線光学式', 500, 'mouse_b.jpg', 0
FROM product_category WHERE name = 'パソコン周辺機器';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '有線ゲーミングマウス', 3800, 'mouse_a.jpg', 0
FROM product_category WHERE name = 'パソコン周辺機器';

INSERT INTO product
    (product_category_id, name, price, image_url, delete_flg)
SELECT id, '無線式キーボード', 1900, 'keybord.jpg', 0
FROM product_category WHERE name = 'パソコン周辺機器';


-- product_stock
INSERT INTO product_stock (product_id, quantity)
SELECT id, 1000
FROM product;
