create table department(
  id integer,
  name VARCHAR(100),
  primary key (id)
);

create sequence seq_department_id
    start with 101
    increment by  1
    maxvalue 2147483647;

create table employee(
  id integer,
  department_id integer,
  name VARCHAR(100),
  name_kana VARCHAR(100),
  primary key (id),
  FOREIGN key (department_id) REFERENCES department(id)
);

create sequence seq_employee_id
    start with 1001
    increment by  1
    maxvalue 2147483647;

create table employee_account(
    id integer,
    employee_id integer,
    name VARCHAR(20),
    password VARCHAR(200),
    primary key (id) ,
    FOREIGN key (employee_id) REFERENCES employee(id)
);

create sequence seq_employee_account_id
    start with 1
    increment by 1
    maxvalue 2147483647;


create table product_category(
    id integer,
    name VARCHAR(20),
    primary key (id)
);


-- create sequence seq_product_category_id
--     start with 1
--     increment by 1
--     as integer;

create table product(
    id integer,
    product_category_id integer,
    name VARCHAR(20),
    price integer,
    image_url VARCHAR(200),
    delete_flg integer,
    primary key (id),
    FOREIGN key (product_category_id) REFERENCES product_category(id)
);

create sequence seq_product_id
    start with 1
    increment by 1
    maxvalue 2147483647;

create table product_stock(
    id integer,
    product_id integer,
    quantity integer,
    primary key (id),
    FOREIGN key (product_id) REFERENCES product(id)
);

create sequence seq_product_stock_id
    start with 1
    increment by 1
    maxvalue 2147483647;

CREATE TABLE order_status (
  id integer,
  name VARCHAR(100),
  primary key (id)
);

create sequence seq_order_status_id
    start with 1
    increment by 1
    maxvalue 2147483647;


CREATE TABLE customer (
  id integer,
  name VARCHAR(20),
  name_kana VARCHAR(20),
  address1 VARCHAR(100),
  address2 VARCHAR(100),
  phone_number VARCHAR(20),
  mail_address  VARCHAR(200),
  username VARCHAR(30),
  password VARCHAR(200),
  register_date TIMESTAMP,
  primary key (id)
);

create sequence seq_customer_id
    start with 1
    increment by 1
    maxvalue 2147483647;

CREATE TABLE payment_method (
  id integer,
  name VARCHAR(100),
  primary key (id)
);

create sequence seq_payment_method_id
    start with 1
    increment by 1
    maxvalue 2147483647;

CREATE TABLE orders (
  id integer,
  customer_id integer,
  order_status_id integer,
  payment_method_id integer,
  order_date TIMESTAMP,
  amount_total integer,
  primary key (id),
  FOREIGN KEY (customer_id) REFERENCES customer(id),
  FOREIGN KEY (order_status_id) REFERENCES order_status(id),
  FOREIGN KEY (payment_method_id) REFERENCES payment_method(id)
);

create sequence seq_orders_id
    start with 1
    increment by 1
    maxvalue 2147483647;

CREATE TABLE order_detail (
  id integer,
  order_id integer,
  product_id integer,
  customer_id integer,
  count integer,
  primary key (id),
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (product_id) REFERENCES product(id),
  FOREIGN KEY (customer_id) REFERENCES customer(id)
);

create sequence seq_order_detail
    start with 1
    increment by 1
    maxvalue 2147483647;



