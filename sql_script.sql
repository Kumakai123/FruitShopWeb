use fruitShop;
-- revenue table
CREATE TABLE IF NOT EXISTS `revenue` (
    `id` CHAR(36) PRIMARY KEY,
    `record_date` date default (now()) NOT NULL,
    `gross_income` DECIMAL(10, 2) NOT NULL DEFAULT 0,
    `net_income` DECIMAL(10, 2) NOT NULL DEFAULT 0,
    `purchases_expense` DECIMAL(10, 2) NOT NULL DEFAULT 0,
    `personnel_expenses` DECIMAL(10, 2) NOT NULL DEFAULT 0,
    `miscellaneous_expense` DECIMAL(10, 2) NOT NULL DEFAULT 0,
    `wastage` DECIMAL(10, 2) NOT NULL DEFAULT 0
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='貨主';


ALTER TABLE `revenue` COMMENT '貨主';
ALTER TABLE `revenue` MODIFY COLUMN `id` CHAR(36) COMMENT '主鍵(UUID)';
ALTER TABLE `revenue` MODIFY COLUMN `record_date` DATE COMMENT '記錄日期';
ALTER TABLE `revenue` MODIFY COLUMN `gross_income` DECIMAL(10, 2) COMMENT '總收入';
ALTER TABLE `revenue` MODIFY COLUMN `net_income` DECIMAL(10, 2) COMMENT '淨收入';
ALTER TABLE `revenue` MODIFY COLUMN `purchases_expense` DECIMAL(10, 2) COMMENT '進貨支出';
ALTER TABLE `revenue` MODIFY COLUMN `personnel_expenses` DECIMAL(10, 2) COMMENT '人事成本';
ALTER TABLE `revenue` MODIFY COLUMN `miscellaneous_expense` DECIMAL(10, 2) COMMENT '雜物成本';
ALTER TABLE `revenue` MODIFY COLUMN `wastage` DECIMAL(10, 2) COMMENT '損耗';

-- consignor table
CREATE TABLE IF NOT EXISTS `consignor` (
    `id` CHAR(36) PRIMARY KEY,
    `lastName` VARCHAR(255) NOT NULL,
    `firstName` VARCHAR(255) NOT NULL,
    `number` VARCHAR(255) NOT NULL,
    `company` VARCHAR(255) NOT NULL,
    UNIQUE KEY `number_company` (`number`, `company`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `consignor` COMMENT '貨主';
ALTER TABLE `consignor` MODIFY COLUMN `id` CHAR(36) COMMENT '主鍵(UUID)';
ALTER TABLE `consignor` MODIFY COLUMN `lastName` VARCHAR(255) COMMENT '姓氏';
ALTER TABLE `consignor` MODIFY COLUMN `firstName` VARCHAR(255) COMMENT '名字';
ALTER TABLE `consignor` MODIFY COLUMN `number` VARCHAR(255) COMMENT '號碼';
ALTER TABLE `consignor` MODIFY COLUMN `company` VARCHAR(255) COMMENT '市場行號';

-- product table
CREATE TABLE IF NOT EXISTS `product` (
    `id` CHAR(36) PRIMARY KEY,
    `product_name` VARCHAR(255) NOT NULL,
    `unit_price` DECIMAL(10, 2) NOT NULL,
    `type` VARCHAR(255) NOT NULL,
    `unit_type` VARCHAR(255) NOT NULL,
    `consignor` CHAR(36) NOT NULL,
    `inventory` FLOAT(10, 2) NOT NULL DEFAULT '0',
    UNIQUE KEY `product_name_unit_price` (`product_name`, `unit_price`),
    FOREIGN KEY (`consignor`) REFERENCES `consignor` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `product` COMMENT '產品';
ALTER TABLE `product` MODIFY COLUMN `id` CHAR(36) COMMENT '主鍵(UUID)';
ALTER TABLE `product` MODIFY COLUMN `product_name` VARCHAR(255) COMMENT '品名';
ALTER TABLE `product` MODIFY COLUMN `unit_price` DECIMAL(10, 2) COMMENT '產品單價';
ALTER TABLE `product` MODIFY COLUMN `type` VARCHAR(255) COMMENT '類型';
ALTER TABLE `product` MODIFY COLUMN `unit_type` VARCHAR(255) COMMENT '單位';
ALTER TABLE `product` MODIFY COLUMN `consignor` CHAR(36) COMMENT '貨主';
ALTER TABLE `product` MODIFY COLUMN `inventory` FLOAT(10, 2) COMMENT '庫存';

-- purchase table
CREATE TABLE IF NOT EXISTS `purchase` (
    `id` CHAR(36) PRIMARY KEY,
    `product` CHAR(36) NOT NULL,
    `quantity` FLOAT(10, 2) NOT NULL DEFAULT '0',
    `date` DATE NOT NULL,
    FOREIGN KEY (`product`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `purchase` COMMENT '貨主';
ALTER TABLE `purchase` MODIFY COLUMN `id` CHAR(36) COMMENT '主鍵(UUID)';
ALTER TABLE `purchase` MODIFY COLUMN `product` CHAR(36) COMMENT '產品';
ALTER TABLE `purchase` MODIFY COLUMN `quantity` FLOAT(10, 2) COMMENT '數量';
ALTER TABLE `purchase` MODIFY COLUMN `date` DATE COMMENT '日期';

-- wastage table
CREATE TABLE IF NOT EXISTS `wastage` (
     `id` CHAR(36) PRIMARY KEY,
    `product` CHAR(36) NOT NULL,
    `quantity` FLOAT(10, 2) NOT NULL DEFAULT '0',
    `date` date default (now()) NOT NULL,
    FOREIGN KEY (`product`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `wastage` COMMENT '貨主';
ALTER TABLE `wastage` MODIFY COLUMN `id` CHAR(36) COMMENT '主鍵(UUID)';
ALTER TABLE `wastage` MODIFY COLUMN `product` CHAR(36) COMMENT '產品';
ALTER TABLE `wastage` MODIFY COLUMN `quantity` FLOAT(10, 2) COMMENT '數量';
ALTER TABLE `wastage` MODIFY COLUMN `date` DATE COMMENT '日期';

CREATE TABLE IF NOT EXISTS `miscellaneous` (
    `id` INT NOT NULL PRIMARY KEY COMMENT '主鍵',
    `name` VARCHAR(255) NOT NULL COMMENT '名稱',
    `amount` DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '花費金額',
    `date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '紀錄日期'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='雜物';

ALTER TABLE `miscellaneous` COMMENT '雜物';
ALTER TABLE `miscellaneous` MODIFY COLUMN `id` INT COMMENT '主鍵';
ALTER TABLE `miscellaneous` MODIFY COLUMN `name` VARCHAR(255) COMMENT '名稱';
ALTER TABLE `miscellaneous` MODIFY COLUMN `amount` DECIMAL(10, 2) COMMENT '花費金額';
ALTER TABLE `miscellaneous` MODIFY COLUMN `date` DATETIME COMMENT '紀錄日期';
