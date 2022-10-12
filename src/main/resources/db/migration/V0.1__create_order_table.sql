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
    warehouseID INT(8) NOT NULL,
    orderID INT(8) NOT NULL UNIQUE,
    orderType INT(8) NOT NULL,
    status VARCHAR(100)
);

CREATE TABLE accounting(
     id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
     username VARCHAR(100) NOT NULL UNIQUE,
     credit INT(8)
);

CREATE TABLE accounting_transaction(
     id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
     accountingID INT(8),
     orderID INT(8) NOT NULL UNIQUE,
     warehouseReservationID INT(8),
     cost INT(8),
     orderType INT(8),
     status VARCHAR(100)
);

CREATE TABLE order_table(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    orderType INT(8) NOT NULL,
    username VARCHAR(100),
    warehouseReservationID INT(8),
    cost INT(8),
    accountingTransactionID INT(8),
    status VARCHAR(100)
);

ALTER TABLE warehouse_reservation ADD CONSTRAINT FK_Warehouse_Reservation_WarehouseID FOREIGN KEY (warehouseID) REFERENCES warehouse(id);
ALTER TABLE accounting_transaction ADD CONSTRAINT FK_Accounting_Transaction_User FOREIGN KEY (accountingID) REFERENCES accounting(id);

INSERT INTO user(username, password, name)VALUES('admin', 'admin', 'admin');
