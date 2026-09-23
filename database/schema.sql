-- Stock Management System - Complete MySQL Schema
CREATE DATABASE IF NOT EXISTS stock_management_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE stock_management_db;

-- 1. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    category_id   INT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL UNIQUE,
    description   VARCHAR(500),
    created_at    DATETIME NOT NULL
) ENGINE=InnoDB;

-- 2. Products Table
CREATE TABLE IF NOT EXISTS products (
    product_id        INT PRIMARY KEY AUTO_INCREMENT,
    sku               VARCHAR(30) NOT NULL UNIQUE,
    name              VARCHAR(100) NOT NULL,
    description       VARCHAR(500),
    category_id       INT NOT NULL,
    unit              VARCHAR(20) NOT NULL DEFAULT 'pcs',
    unit_price        DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    quantity_in_stock INT NOT NULL DEFAULT 0,
    reorder_level     INT NOT NULL DEFAULT 10,
    created_at        DATETIME NOT NULL,
    updated_at        DATETIME NOT NULL,
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(category_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 3. Suppliers Table
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id     INT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         VARCHAR(250),
    created_at      DATETIME NOT NULL,
    updated_at      DATETIME NOT NULL
) ENGINE=InnoDB;

-- 4. Stock Entry Table (Incoming Logistics)
CREATE TABLE IF NOT EXISTS stock_entries (
    entry_id      INT PRIMARY KEY AUTO_INCREMENT,
    product_id    INT NOT NULL,
    supplier_id   INT NOT NULL,
    quantity      INT NOT NULL,
    entry_date    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remarks       VARCHAR(500),
    CONSTRAINT fk_entries_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_entries_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 5. Stock Exit Table (Outgoing Logistics / Sales)
CREATE TABLE IF NOT EXISTS stock_exits (
    exit_id       INT PRIMARY KEY AUTO_INCREMENT,
    product_id    INT NOT NULL,
    quantity      INT NOT NULL,
    exit_date     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason        VARCHAR(255),
    CONSTRAINT fk_exits_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 6. Alert Rules Table (System Warnings Settings)
CREATE TABLE IF NOT EXISTS alert_rules (
    rule_id                    INT PRIMARY KEY AUTO_INCREMENT,
    product_id                 INT NOT NULL UNIQUE,
    minimum_threshold_quantity INT NOT NULL DEFAULT 10,
    severity_level             VARCHAR(20) NOT NULL DEFAULT 'WARNING',
    CONSTRAINT fk_alerts_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Seed default category so the application is ready to use immediately
INSERT INTO categories (name, description, created_at)
SELECT * FROM (SELECT 'General' AS name, 'Default uncategorized items' AS description, NOW() AS created_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'General');
