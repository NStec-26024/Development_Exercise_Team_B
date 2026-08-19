drop table    if exists employee_account;
drop table    if exists employee;
drop table    if exists department;
drop table    if exists product_stock;
drop table    if exists order_detail;
drop table    if exists product;
drop table    if exists product_category;
drop table    if exists orders;
drop table    if exists payment_method;
drop table    if exists order_status;
drop table    if exists customer;

drop sequence if exists employee_account_id_seq;
drop sequence if exists employee_id_seq;
drop sequence if exists department_id_seq;
drop sequence if exists product_stock_id_seq;
drop sequence if exists order_detail_id_seq;
drop sequence if exists product_id_seq;
drop sequence if exists product_category_id_seq;
drop sequence if exists orders_id_seq;
drop sequence if exists payment_method_id_seq;
drop sequence if exists order_status_id_seq;
drop sequence if exists customer_id_seq;

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


