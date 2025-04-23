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

3. **Install MySQL Workbench** (for database management)
   - Open your web browser and go to https://dev.mysql.com/downloads/workbench/
   - Click on "Download" for macOS
   - Once downloaded, open the installer package and follow the on-screen instructions

4. **Install Maven**
   - Open your web browser and go to https://maven.apache.org/download.cgi
   - Download the Binary zip archive (apache-maven-x.x.x-bin.zip)
   - Unzip the downloaded file to your Applications folder

### Step 2: Set Up the Database

1. **Open MySQL Workbench**
   - Open MySQL Workbench from your Applications folder
   - Connect to your MySQL server by clicking on the connection that shows "localhost"
   - You'll be prompted to enter the root password you created during MySQL installation

2. **Create the database**
   - In MySQL Workbench, click on the SQL icon to create a new query tab
   - Type: `CREATE DATABASE school_supplies_db;`
   - Click the lightning bolt icon to execute the query

3. **Import the database structure**
   - In MySQL Workbench, go to File → Open SQL Script
   - Navigate to the project folder and open the file at `database/school_supplies_db.sql`
   - Click the lightning bolt icon to execute the script

### Step 3: Configure and Run the Application

1. **Update the database password**
   - Open the project folder
   - Navigate to `src/main/java/config`
   - Open the file `DatabaseConfig.java` in a text editor
   - Find the line with `private static final String PASSWORD = "1234";`
   - Change "1234" to your MySQL root password (keep the quotes)
   - Save the file

2. **Create a run script for convenience** (optional)
   - Open TextEdit
   - Create a new file with the following content:
     ```
     #!/bin/bash
     java -jar target/inventory-management-1.0-SNAPSHOT-jar-with-dependencies.jar
     ```
   - Save the file as `run.command` in the main project folder
   - Open Terminal
   - Make the script executable by typing: `chmod +x run.command`
   - Now you can double-click the run.command file to start the application

3. **Run the application**
   - If you downloaded the application as a JAR file:
     - Double-click on the JAR file or right-click and select "Open With" → "Java Launcher"
   - If you downloaded the source code:
     - Double-click on the included `run.command` file (if available)
     - Or follow the build instructions below

### Building from Source Code (optional)

If you need to build the application from source:

1. **Open Terminal**
   - Go to Applications → Utilities → Terminal

2. **Navigate to the project folder**
   - Type `cd ` (with a space after cd)
   - Drag the project folder into the Terminal window and press Enter

3. **Build with Maven**
   - Type `./mvnw clean package` and press Enter
   - Wait for the build to complete

4. **Run the application**
   - Type `java -jar target/inventory-management-1.0-SNAPSHOT-jar-with-dependencies.jar` and press Enter

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

3. **Install Maven** (optional - only needed if building from source)
   - Open your web browser and go to https://maven.apache.org/download.cgi
   - Download the Binary zip archive
   - Extract the zip file to a folder like C:\Program Files\Maven

### Step 2: Set Up the Database

1. **Open MySQL Workbench**
   - Click Start → MySQL → MySQL Workbench
   - Connect to your MySQL server by clicking on the connection that shows "localhost"
   - Enter the root password you created during MySQL installation

2. **Create the database**
   - In MySQL Workbench, click on the SQL icon to create a new query tab
   - Type: `CREATE DATABASE school_supplies_db;`
   - Click the lightning bolt icon to execute the query

3. **Import the database structure**
   - In MySQL Workbench, go to File → Open SQL Script
   - Navigate to the project folder and open the file at `database/school_supplies_db.sql`
   - Click the lightning bolt icon to execute the script

### Step 3: Configure and Run the Application

1. **Update the database password**
   - Open the project folder
   - Navigate to `src/main/java/config`
   - Open the file `DatabaseConfig.java` in a text editor (like Notepad)
   - Find the line with `private static final String PASSWORD = "1234";`
   - Change "1234" to your MySQL root password (keep the quotes)
   - Save the file

2. **Create a run script for convenience** (optional)
   - Open Notepad
   - Create a new file with the following content:
     ```
     java -jar target/inventory-management-1.0-SNAPSHOT-jar-with-dependencies.jar
     pause
     ```
   - Save the file as `run.bat` in the main project folder
   - Now you can double-click the run.bat file to start the application

3. **Run the application**
   - If you downloaded the application as a JAR file:
     - Double-click on the JAR file or right-click and select "Open With" → "Java(TM) Platform SE Binary"
   - If you downloaded the source code:
     - Double-click on the included `run.bat` file (if available)
     - Or follow the build instructions below

### Building from Source Code (optional)

If you need to build the application from source:

1. **Open Command Prompt**
   - Click Start → type "cmd" → press Enter

2. **Navigate to the project folder**
   - Type `cd ` (with a space after cd)
   - Drag the project folder into the Command Prompt window and press Enter

3. **Build with Maven**
   - Type `mvnw.cmd clean package` and press Enter
   - Wait for the build to complete

4. **Run the application**
   - Type `java -jar target/inventory-management-1.0-SNAPSHOT-jar-with-dependencies.jar` and press Enter

## Troubleshooting

### The application won't start

- Make sure Java is installed correctly
- Check that the JAR file is in the right location

### Database connection error

- Verify MySQL is running
- Make sure the password in `DatabaseConfig.java` matches your MySQL root password
- Ensure the database `school_supplies_db` exists

### Other issues

- Try restarting your computer
- Make sure all installation steps were completed successfully
