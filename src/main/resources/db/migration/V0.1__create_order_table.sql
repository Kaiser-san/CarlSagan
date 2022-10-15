CREATE TABLE user(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100)
);

CREATE TABLE warehouse_stock(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_type INT(8) NOT NULL UNIQUE,
    cost INT(8),
    stock INT(8)
);

CREATE TABLE warehouse_stock_version_file(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_id INT(8) NOT NULL UNIQUE,
    order_type INT(8) NOT NULL UNIQUE,
    status VARCHAR(100)
);

CREATE TABLE accounting(
     id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
     username VARCHAR(100) NOT NULL UNIQUE,
     credit INT(8)
);

CREATE TABLE accounting_transaction(
     id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
     accounting_id INT(8),
     order_id INT(8) NOT NULL UNIQUE,
     cost INT(8),
     order_type INT(8)
);

CREATE TABLE accounting_transaction_version_file(
     id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
     accounting_id INT(8),
     order_id INT(8) NOT NULL,
     cost INT(8),
     order_type INT(8),
     status VARCHAR(100)
);

CREATE TABLE order_table(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_type INT(8) NOT NULL,
    username VARCHAR(100),
    cost INT(8),
    accounting_transaction_id INT(8),
    status VARCHAR(100)
);

ALTER TABLE accounting_transaction ADD CONSTRAINT FK_Accounting_Transaction_User FOREIGN KEY (accounting_id) REFERENCES accounting(id);
ALTER TABLE accounting_transaction_version_file ADD CONSTRAINT FK_Accounting_Transaction_Version_Accounting FOREIGN KEY (accounting_id) REFERENCES accounting(id);

INSERT INTO user(username, password, name) VALUES ('admin', 'admin', 'admin');
INSERT INTO accounting(username, credit) VALUES ('admin', 10000);
INSERT INTO warehouse_stock(order_type, cost, stock) VALUES (1, 100, 5);
