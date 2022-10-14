CREATE TABLE user(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100)
);

CREATE TABLE warehouse(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    cost INT(8)
);

CREATE TABLE warehouse_reservation(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    warehouse_id INT(8) NOT NULL,
    order_id INT(8) NOT NULL UNIQUE,
    order_type INT(8) NOT NULL,
    status VARCHAR(100)
);

CREATE TABLE warehouse_reservation_version_file(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_id INT(8) NOT NULL UNIQUE,
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
     warehouse_reservation_id INT(8),
     cost INT(8),
     order_type INT(8)
);

CREATE TABLE accounting_transaction_version_file(
     id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
     accounting_id INT(8),
     order_id INT(8) NOT NULL,
     warehouse_reservation_id INT(8),
     cost INT(8),
     order_type INT(8),
     status VARCHAR(100)
);

CREATE TABLE order_table(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_type INT(8) NOT NULL,
    username VARCHAR(100),
    warehouse_reservation_id INT(8),
    cost INT(8),
    accounting_transaction_id INT(8),
    status VARCHAR(100)
);

ALTER TABLE warehouse_reservation ADD CONSTRAINT FK_Warehouse_Reservation_WarehouseID FOREIGN KEY (warehouse_id) REFERENCES warehouse(id);
ALTER TABLE accounting_transaction ADD CONSTRAINT FK_Accounting_Transaction_User FOREIGN KEY (accounting_id) REFERENCES accounting(id);
ALTER TABLE accounting_transaction_version_file ADD CONSTRAINT FK_Accounting_Transaction_Version_Accounting FOREIGN KEY (accounting_id) REFERENCES accounting(id);

INSERT INTO user(username, password, name) VALUES ('admin', 'admin', 'admin');
INSERT INTO accounting(username, credit) VALUES ('admin', 10000);
INSERT INTO warehouse(name, cost) VALUES ('first', 100);
