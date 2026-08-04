create table employee(
    primary key (id) integer,
    FOREIGN key (department_id) REFERENCES department(id),
    name VARCHAR(100),
    kana VARCHAR(100)    
);

create seaquence seq_employee_id(
    start with 1
    increment by 
    as integer
);

create table employee_account(
    primary key (id) integer,
    FOREIGN key (employee_id) REFERENCES employee(id),
    name VARCHAR(100),
    password VARCHAR(100)

);

create seaquence seq_employee_account_id(
    start with 1
    increment by
    as integer
);

create table product_category(
    primary key (id) integer,
    name VARCHAR(20)
);


create seaquence seq_product_category_id(
    start with 1
    increment by 
    as integer
);

create table product(
    primary key (id) integer,
    FOREIGN key (product_category_id) REFERENCES product_category(id),
    name VARCHAR(100),
    price integer,
    image_url VARCHAR(200),
    delete_flg integer
);

create seaquence seq_product_id(
    start with 1
    increment by 
    as integer
);

create table product_stock(
    primary key (id) integer,
    FOREIGN key (product_id) REFERENCES product(id),
    quantity integer

);

create seaquence seq_product_stock_id(
    start with 1
    increment by
    as integer
);





create table payment_method(
    primary key (id) integer,
    name VARCHAR(100)
);

create seaquence seq_payment_method_id(
    start with 1
    increment by 
);
