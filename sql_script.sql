create database fruitShop;
use fruitShop;
-- revenue table
CREATE TABLE IF NOT EXISTS fruitshop.revenue
(
    id                    VARCHAR(36)    NOT NULL PRIMARY KEY COMMENT '主鍵(UUID)',
    record_date           DATE           NOT NULL COMMENT '記錄日期',
    gross_income          DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '總收入',
    net_income            DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '淨收入',
    purchases_expense     DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '進貨支出',
    personnel_expenses    DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '人事成本',
    miscellaneous_expense DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '雜物成本',
    wastage               DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '損耗'

) COMMENT ='營運狀況';

-- person table
CREATE TABLE IF NOT EXISTS fruitshop.person
(
    id        VARCHAR(36)  NOT NULL PRIMARY KEY COMMENT '主鍵(UUID)',
    nick_name VARCHAR(100) NOT NULL COMMENT '暱稱/稱呼',
    name      VARCHAR(100) DEFAULT NULL COMMENT '名字',
    level     VARCHAR(50)  NOT NULL COMMENT '身分等級',
    email     VARCHAR(100) DEFAULT NULL COMMENT '信箱',
    password  VARCHAR(100) DEFAULT NULL COMMENT '密碼',
    number    VARCHAR(50)  NOT NULL COMMENT '號碼',
    company   VARCHAR(100) NOT NULL COMMENT '市場行號',
    UNIQUE KEY uq_number_company (number, company)
) COMMENT '人員';


-- product table
CREATE TABLE IF NOT EXISTS fruitshop.product
(
    id           VARCHAR(36)    NOT NULL PRIMARY KEY COMMENT '主鍵(UUID)',
    product_name VARCHAR(100)   NOT NULL COMMENT '品名',
    unit_price   DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '產品單價',
    type         VARCHAR(50)             DEFAULT NULL COMMENT '類型',
    unit_type    VARCHAR(50)             DEFAULT NULL COMMENT '單位',
    person       CHAR(36)                DEFAULT NULL COMMENT '貨主',
    inventory    DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '庫存',
    UNIQUE KEY uq_product_name_unit_price (product_name, unit_price),
    CONSTRAINT fk_product_person FOREIGN KEY (person)
        REFERENCES fruitshop.person (id)
        ON UPDATE CASCADE ON DELETE SET NULL
) COMMENT ='產品';

-- purchase table
CREATE TABLE IF NOT EXISTS fruitshop.purchase
(
    id             VARCHAR(36)    NOT NULL PRIMARY KEY COMMENT '主鍵(UUID)',
    product        CHAR(36)       NOT NULL COMMENT '產品',
    quantity       DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '數量',
    order_date     DATETIME       DEFAULT CURRENT_TIMESTAMP
                                  NOT NULL COMMENT '開單日期',
    receiving_date DATETIME       NOT NULL COMMENT '進貨日期',
    CONSTRAINT fk_purchase_product FOREIGN KEY (product)
        REFERENCES fruitshop.product (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT ='進貨表';

-- wastage table
CREATE TABLE IF NOT EXISTS fruitshop.wastage
(
    id       VARCHAR(36)    NOT NULL PRIMARY KEY COMMENT '主鍵(UUID)',
    product  CHAR(36)       NOT NULL COMMENT '產品',
    quantity DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '數量',
    date     DATE           NOT NULL DEFAULT (CURRENT_DATE) COMMENT '日期',
    CONSTRAINT fk_wastage_product FOREIGN KEY (product)
        REFERENCES fruitshop.product (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT ='損耗表';

-- miscellaneous
CREATE TABLE IF NOT EXISTS `miscellaneous`
(
    `id`     VARCHAR(36)    NOT NULL PRIMARY KEY COMMENT '主鍵',
    `name`   VARCHAR(255)   NOT NULL COMMENT '名稱',
    `amount` DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '花費金額',
    `date`   DATETIME       NOT NULL DEFAULT
    CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '紀錄日期'
) COMMENT ='雜物';