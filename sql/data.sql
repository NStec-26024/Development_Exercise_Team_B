WITH 
    /* departmentテーブルにデータを挿入 */
    inserted_department AS(
        INSERT INTO department (name) 
            VALUES  ('人事部'),
                    ('企画部'),
                    ('システム開発部')
        RETURNING id AS department_id, name
    ),

    /* employeeテーブルにデータを挿入 */
    inserted_employee AS(
        INSERT INTO employee (department_id, name, name_kana) (
            SELECT department_id, '山田太郎', 'ヤマダタロウ'
                FROM inserted_department
                WHERE name = '人事部'
                UNION ALL
            SELECT department_id, '川田次郎', 'カワタジロウ'
                FROM inserted_department
                WHERE name = '企画部'
                UNION ALL
            SELECT department_id, '海田三郎', 'ウミタサブロウ'
                FROM inserted_department
                WHERE name = 'システム開発部'
        )
        
        RETURNING id AS employee_id, name
    ),

    /* employee_accountテーブルにデータを挿入 */
    inserted_employee_account AS (
        INSERT INTO employee_account (employee_id, name, password)(
            SELECT employee_id, 'yamadatarou1001', 'yamadapassword1001'
                FROM inserted_employee
                WHERE name = '山田太郎'
        )
    ),

    /* product_categoryテーブルにデータを挿入 */
    inserted_category AS (        
        INSERT INTO product_category (name)
            VALUES  ('文具'),
                    ('雑貨'),
                    ('パソコン周辺機器')
        RETURNING id AS product_category_id, name
    ),

    /* productテーブルにデータを挿入 */
    inserted_product AS(
        INSERT INTO product (product_category_id, name, price, image_url, delete_flg)(
            SELECT product_category_id, 'マーカー(青)', 110, 'blue_maker.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '油性ボールペン(青)', 111, 'blue_pen_o.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '水性ボールペン(青)', 112, 'blue_pen_w.jpeg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '鉛筆', 113, 'black_pen.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '油性ボールペン(黒)', 114, 'black_pen_o.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '水性ボールペン(黒)', 115, 'black_pen_w.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '筆ペン(黒)', 116, 'black_fudepen.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '鉛筆(赤)', 117, 'red_pen.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, 'マーカー(ピンク)', 118, 'red_maker.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '油性ボールペン(赤)', 119, 'red_pen_o.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '水性ボールペン(赤)', 120, 'red_pen_w.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '筆ペン(赤)', 121, 'red_fudepen.jpeg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, 'マーカー(黄)', 122, 'yellow_maker.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, 'マーカー(緑)', 123, 'green_maker.jpg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '色鉛筆セット(12色)', 124, 'color_pen12.jpeg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL
            SELECT product_category_id, '色鉛筆セット(24色)', 125, 'color_pen48.jpeg', 0
                FROM inserted_category WHERE name = '文具' UNION ALL

            SELECT product_category_id, 'トートバッグ', 1500, 'bag.jpg', 0
                FROM inserted_category WHERE name = '雑貨' UNION ALL
            SELECT product_category_id, 'ハンカチ', 800, 'cloth.jpg', 0
                FROM inserted_category WHERE name = '雑貨' UNION ALL
            SELECT product_category_id, 'ストラップ付キーホルダー(二重リング)', 700, 'keyholder.jpg', 0
                FROM inserted_category WHERE name = '雑貨' UNION ALL
            SELECT product_category_id, 'ワンタッチキーホルダー', 1000, 'keyholder2.jpeg', 0
                FROM inserted_category WHERE name = '雑貨' UNION ALL
            SELECT product_category_id, 'アイマスク', 1300, 'mask.jpg', 0
                FROM inserted_category WHERE name = '雑貨' UNION ALL
            SELECT product_category_id, '防水スプレー', 1200, 'spray.jpg', 0
                FROM inserted_category WHERE name = '雑貨' UNION ALL
            SELECT product_category_id, '折りたたみ傘', 2000, 'umbrella.jpg', 0
                FROM inserted_category WHERE name = '雑貨' UNION ALL
                
            SELECT product_category_id, 'ワイヤレスマウス', 900, 'mouse_c.jpg', 0
                FROM inserted_category WHERE name = 'パソコン周辺機器' UNION ALL
            SELECT product_category_id, 'ワイヤレストラックボール', 1300, 'mouse_d.jpeg', 0
                FROM inserted_category WHERE name = 'パソコン周辺機器' UNION ALL
            SELECT product_category_id, '有線光学式', 500, 'mouse_b.jpg', 0
                FROM inserted_category WHERE name = 'パソコン周辺機器' UNION ALL
            SELECT product_category_id, '有線ゲーミングマウス', 3800, 'mouse_a.jpg', 0
                FROM inserted_category WHERE name = 'パソコン周辺機器' UNION ALL
            SELECT product_category_id, '無線式キーボード', 1900, 'keybord.jpg', 0
                FROM inserted_category WHERE name = 'パソコン周辺機器' 
        )

        RETURNING id AS product_id
    )


/* product_stockテーブルにデータを挿入 */
INSERT INTO product_stock (product_id, quantity)
    SELECT  product_id, 1000
    FROM inserted_product;
