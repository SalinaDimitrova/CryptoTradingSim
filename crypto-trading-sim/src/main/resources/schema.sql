
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS portfolio;
DROP TABLE IF EXISTS account;

CREATE TABLE IF NOT EXISTS account
(
    id INT UNSIGNED PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    balance DECIMAL(10,2) NOT NULL DEFAULT 10000.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD'
);

CREATE TABLE IF NOT EXISTS portfolio
(
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    account_id INT UNSIGNED NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    quantity DECIMAL(10, 5) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS orders
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT UNSIGNED,
    symbol VARCHAR(10) NOT NULL ,
    quantity DECIMAL(10, 5) NOT NULL ,
    price DECIMAL(10, 2) NOT NULL,
    type ENUM ('BUY', 'SELL') NOT NULL,
    status ENUM('ORDERED', 'EXECUTED', 'FAILED') NOT NULL,
    profit_loss DECIMAL(10, 2),
    time_ordered DATETIME NOT NULL,
    time_executed DATETIME,
    FOREIGN KEY (account_id) REFERENCES account(id)
);