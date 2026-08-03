WITH 
    /* departmentテーブルにデータを挿入 */
    inserted_department AS(
        INSERT INTO department (id, name) 
            VALUES  (nextval('seq_department_id'), '人事部'),
                    (nextval('seq_department_id'), '企画部'),
                    (nextval('seq_department_id'), 'システム開発部')
        RETURNING id AS department_id, name
    ),

    /* employeeテーブルにデータを挿入 */
    inserted_employee AS(
        INSERT INTO employee (id, department_id, name, name_kana) (
            SELECT nextval('seq_employee_id'), department_id, '山田太郎', 'ヤマダタロウ'
                FROM inserted_department
                WHERE name = '人事部'
                UNION ALL
            SELECT nextval('seq_employee_id'), department_id, '川田次郎', 'カワタジロウ'
                FROM inserted_department
                WHERE name = '企画部'
                UNION ALL
            SELECT nextval('seq_employee_id'), department_id, '海田三郎', 'ウミタサブロウ'
                FROM inserted_department
                WHERE name = 'システム開発部'
        )
        
        RETURNING id AS employee_id, name
    ),

    /* employee_accountテーブルにデータを挿入 */
    inserted_employee_account AS (
        INSERT INTO employee_account (id, employee_id, name, password)(
            SELECT nextval('seq_employee_account_id'), employee_id, 'yamadatarou1001', 'yamadapassword1001'
                FROM inserted_employee
                WHERE name = '山田太郎'
        )
    ),

    /* product_categoryテーブルにデータを挿入 */
    inserted_category AS (        
        INSERT INTO product_category (id, name)
            VALUES  (1, '文具'),
                    (2, '雑貨'),
                    (3, 'パソコン周辺機器')
        RETURNING id AS product_category_id
    ),

    /* productテーブルにデータを挿入 */
    inserted_product AS(
        INSERT INTO product (id, product_category_id, name, price, image_url, delete_flg)
            VALUES   (nextval('seq_product_id'), 1, '水性ボールペン(黒)', '110', 'rollerball-pen_black.jpg', 0),
                    (nextval('seq_product_id'), 1, '水性ボールペン(赤)', '110', 'rollerball-pen_red.jpg', 0),
                    (nextval('seq_product_id'), 1, '水性ボールペン(青)', '110', 'rollerball-pen_blue.jpg', 0),
                    (nextval('seq_product_id'), 1, '油性ボールペン(黒)', '110', 'ballpoint-pen_black.jpg', 0),

                    (nextval('seq_product_id'), 2, 'クッション', '2500', 'cushion.jpg', 0),
                    (nextval('seq_product_id'), 2, 'アロマデュフューザー', '3000', 'aroma-diffuser.jpg', 0),
                    (nextval('seq_product_id'), 2, 'マグカップ', '10000', 'mug.jpg', 0),
                    (nextval('seq_product_id'), 2, 'トートバッグ', '1500', 'tote-bag.jpg', 0),

                    (nextval('seq_product_id'), 3, 'ワイヤレスマウス', '900', 'wireless-mouse.jpg', 0),
                    (nextval('seq_product_id'), 3, 'ワイヤレストラックボール', '1300', 'wireless-trackball-mouse.jpg', 0),
                    (nextval('seq_product_id'), 3, '無線ゲーミングマウス', '12000', 'wireless-gaming-mouse.jpg', 0),
                    (nextval('seq_product_id'), 3, 'USB有線式キーボード', '1400', 'wired-usb-keyboard.jpg', 0)

        RETURNING id AS product_id
    )


/* product_stockテーブルにデータを挿入 */
INSERT INTO product_stock (id, product_id, quantity)
    SELECT  nextval('seq_product_stock_id'), product_id, 1000
    FROM inserted_product;
