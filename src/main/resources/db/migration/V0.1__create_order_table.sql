CREATE TABLE user(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100)
);

CREATE TABLE kitchen(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    cost INT(8)
);

CREATE TABLE kitchen_appointment(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    kitchen_id INT(8) NOT NULL,
    order_id INT(8) NOT NULL UNIQUE,
    order_type INT(8) NOT NULL,
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
     username VARCHAR(100),
     kitchen_appointment_id INT(8),
     order_type INT(8),
     status VARCHAR(100)
);

CREATE TABLE order_table(
    id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_type INT(8) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100),
    kitchen_appointments_id INT(8),
    accounting_transactions_id INT(8),
    status VARCHAR(100)
);

ALTER TABLE kitchen_appointment ADD CONSTRAINT FK_Kitchen_Appointment_KitchenID FOREIGN KEY (kitchen_id) REFERENCES kitchen(id);
ALTER TABLE accounting_transaction ADD CONSTRAINT FK_Accounting_Transaction_User FOREIGN KEY (accounting_id) REFERENCES accounting(id);

INSERT INTO user(username, password, name)VALUES('admin', 'admin', 'admin');
