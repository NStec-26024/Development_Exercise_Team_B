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

create table department(
  id serial,
  name VARCHAR(100),
  primary key (id)
);

create table employee(
  id serial,
  department_id integer,
  name VARCHAR(100),
  name_kana VARCHAR(100),
  primary key (id),
  FOREIGN key (department_id) REFERENCES department(id)
);

create table employee_account(
    id serial,
    employee_id integer,
    name VARCHAR(20),
    password VARCHAR(255),
    primary key (id) ,
    FOREIGN key (employee_id) REFERENCES employee(id)
);

create table product_category(
    id serial,
    name VARCHAR(30),
    primary key (id)
);

create table product(
    id serial,
    product_category_id integer,
    name VARCHAR(30),
    price integer,
    image_url VARCHAR(200),
    delete_flg integer,
    primary key (id),
    FOREIGN key (product_category_id) REFERENCES product_category(id)
);

create table product_stock(
    id serial,
    product_id integer,
    quantity integer,
    primary key (id),
    FOREIGN key (product_id) REFERENCES product(id)
);

CREATE TABLE order_status (
  id serial,
  name VARCHAR(100),
  primary key (id)
);

CREATE TABLE customer (
  id serial,
  name VARCHAR(20),
  name_kana VARCHAR(20),
  address1 VARCHAR(100),
  address2 VARCHAR(100),
  phone_number VARCHAR(20),
  mail_address  VARCHAR(200),
  username VARCHAR(30),
  password VARCHAR(255),
  register_date TIMESTAMP,
  primary key (id)
);

CREATE TABLE payment_method (
  id serial,
  name VARCHAR(100),
  primary key (id)
);

CREATE TABLE orders (
  id serial,
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

CREATE TABLE order_detail (
  id serial,
  order_id integer,
  product_id integer,
  customer_id integer,
  unit_price integer,
  count integer,
  primary key (id),
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (product_id) REFERENCES product(id),
  FOREIGN KEY (customer_id) REFERENCES customer(id)
);


