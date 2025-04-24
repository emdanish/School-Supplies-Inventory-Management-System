# School Supplies Inventory Management System

A comprehensive Java-based desktop application for managing school supplies inventory with user authentication, inventory tracking, and reporting features.

## Table of Contents
- [Features](#features)
- [Login Credentials](#login-credentials)
- [Running on Mac OS](#running-on-mac-os)
- [Running on Windows](#running-on-windows)
- [Screenshots](#screenshots)
- [Troubleshooting](#troubleshooting)

## Features

- User Authentication (Admin and Staff roles)
- Inventory Management
- Categories and Suppliers
- Transactions Tracking
- Reports Generation
- User Management (Admin only)

## Login Credentials

- **Admin User**
  - Username: `admin`
  - Password: `admin123`

- **Staff User**
  - Username: `staff1`
  - Password: `staff123`

## Running on Mac OS

### Step 1: Install Required Software

1. **Install Java Development Kit (JDK)**
   - Open your web browser and go to https://adoptium.net/
   - Download the macOS installer for JDK 11 or higher
   - Once downloaded, open the installer package and follow the on-screen instructions
   - When the installation is complete, restart your computer

2. **Install MySQL**
   - Open your web browser and go to https://dev.mysql.com/downloads/mysql/
   - Click on "Download" for macOS
   - Once downloaded, open the installer package and follow the on-screen instructions
   - **Important**: During installation, you'll be asked to set a root password. Remember this password as you'll need it later!
   - After installation, MySQL should start automatically

### Step 2: Set Up the Database

1. **Using the setup script (Recommended)**
   - Open Terminal (Applications → Utilities → Terminal)
   - Navigate to the project folder with `cd /path/to/project`
   - Run the setup script with: `mysql -u root -p < setup_database.sql`
   - Enter your MySQL root password when prompted
   - This will create the database, user, tables, and sample data

2. **Manual Setup (Alternative)**
   - Install MySQL Workbench from https://dev.mysql.com/downloads/workbench/
   - Open MySQL Workbench and connect to your local MySQL server
   - Click the SQL icon to create a new query tab
   - Copy the contents of `setup_database.sql` into the query tab
   - Execute the query using the lightning bolt icon

### Step 3: Run the Application

1. **Build the Application**
   - Open Terminal
   - Navigate to the project folder with `cd /path/to/project`
   - Build using Maven Wrapper: `./mvnw clean package`
   - Wait for the build to complete

2. **Run the Application**
   - In the same Terminal window, run:
   ```
   java -jar target/inventory-management-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

3. **Create a Launcher Script (Optional)**
   - Create a file named `run.command` with the following content:
   ```
   #!/bin/bash
   cd "$(dirname "$0")"
   java -jar target/inventory-management-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```
   - Make it executable: `chmod +x run.command`
   - Now you can launch the application by double-clicking `run.command`

## Running on Windows

### Step 1: Install Required Software

1. **Install Java Development Kit (JDK)**
   - Open your web browser and go to https://adoptium.net/
   - Download the Windows installer for JDK 11 or higher
   - Run the installer and follow the on-screen instructions
   - Make sure to select the option to add Java to your PATH during installation

2. **Install MySQL**
   - Open your web browser and go to https://dev.mysql.com/downloads/installer/
   - Download the MySQL Installer
   - Run the installer and select "MySQL Server" and "MySQL Workbench"
   - Follow the on-screen instructions
   - **Important**: During installation, you'll be asked to set a root password. Remember this password!

### Step 2: Set Up the Database

1. **Using the setup script (Recommended)**
   - Open Command Prompt (Start → type "cmd" → press Enter)
   - Navigate to the project folder with `cd \path\to\project`
   - Run the setup script with: `mysql -u root -p < setup_database.sql`
   - Enter your MySQL root password when prompted
   - This will create the database, user, tables, and sample data

2. **Manual Setup (Alternative)**
   - Open MySQL Workbench from the Start menu
   - Connect to your local MySQL server
   - Click the SQL icon to create a new query tab
   - Copy the contents of `setup_database.sql` into the query tab
   - Execute the query using the lightning bolt icon

### Step 3: Run the Application

1. **Build the Application**
   - Open Command Prompt
   - Navigate to the project folder with `cd \path\to\project`
   - Build using Maven Wrapper: `mvnw.cmd clean package`
   - Wait for the build to complete

2. **Run the Application**
   - In the same Command Prompt window, run:
   ```
   java -jar target/inventory-management-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

3. **Create a Launcher Script (Optional)**
   - Create a file named `run.bat` with the following content:
   ```
   @echo off
   java -jar target/inventory-management-1.0-SNAPSHOT-jar-with-dependencies.jar
   pause
   ```
   - Now you can launch the application by double-clicking `run.bat`

## How the Database Setup Works

The `setup_database.sql` script does the following:
1. Creates a database called `school_supplies_db`
2. Creates a database user `school_supplies_user` with password `1234`
3. Grants necessary permissions to this user
4. Creates all required tables (categories, suppliers, items, transactions, users)
5. Adds sample data including default users

**Note**: If you need to customize the database connection, you can edit:
```
src/main/java/config/DatabaseConfig.java
```

## Troubleshooting

### The application won't start

- Make sure Java is installed correctly
- Check that the JAR file is in the right location

### Database connection error

- Verify MySQL is running
- Make sure the MySQL user `school_supplies_user` exists with password `1234`
- Ensure the database `school_supplies_db` exists
- Check network connectivity if using a remote database

### Login issues

- The default users (admin/admin123 and staff1/staff123) should work out of the box
- If you can't login, ensure the database setup was successful
- Check the application logs for any SQL errors

### Other issues

- Try running the setup_database.sql script again to reset the database
- Delete the target folder and rebuild the application
- Make sure all installation steps were completed successfully
