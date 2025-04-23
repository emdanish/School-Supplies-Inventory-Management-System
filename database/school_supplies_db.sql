CREATE DATABASE IF NOT EXISTS school_supplies_db;

USE school_supplies_db;

-- Create Categories Table
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

-- Create Suppliers Table
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_info VARCHAR(255) NOT NULL
);

-- Create Items Table
CREATE TABLE IF NOT EXISTS items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    category_id INT NOT NULL,
    quantity INT NOT NULL,
    supplier_id INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    reorder_level INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- Create Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    type ENUM('IN', 'OUT') NOT NULL,
    quantity INT NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(item_id)
);

-- Create Users Table for Authentication
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    fullname VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'STAFF') NOT NULL
);

-- Insert sample data for categories
INSERT INTO categories (category_name) VALUES 
('Books'),
('Stationery'),
('Art Supplies'),
('Lab Equipment'),
('Office Supplies');

-- Insert sample data for suppliers
INSERT INTO suppliers (supplier_name, contact_info) VALUES 
('ABC Supplies Co.', 'contact@abcsupplies.com, 123-456-7890'),
('XYZ Educational Materials', 'sales@xyzedu.com, 987-654-3210'),
('Stationery Emporium', 'orders@stationeryemporium.com, 555-123-4567'),
('Lab Supplies Inc.', 'info@labsupplies.com, 555-987-6543'),
('Office Wares Ltd.', 'support@officewares.com, 555-789-0123');

-- Insert sample admin user (username: admin, password: admin123)
INSERT INTO users (username, password, fullname, role) VALUES
('admin', 'admin123', 'System Administrator', 'ADMIN'),
('staff1', 'staff123', 'Staff Member 1', 'STAFF'); 