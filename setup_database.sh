#!/bin/bash

# Prompt for MySQL root password
read -s -p "Enter MySQL root password: " root_password
echo

# Create database and user
mysql -u root -p"$root_password" << EOF
CREATE DATABASE IF NOT EXISTS school_supplies_db;
USE school_supplies_db;

-- Create user if not exists
CREATE USER IF NOT EXISTS 'school_supplies_user'@'localhost' IDENTIFIED BY 'school123';
GRANT ALL PRIVILEGES ON school_supplies_db.* TO 'school_supplies_user'@'localhost';
FLUSH PRIVILEGES;

-- Create tables and insert data
$(cat database/school_supplies_db.sql)
EOF

echo "Database setup completed!" 