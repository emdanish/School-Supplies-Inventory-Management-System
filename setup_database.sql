-- Drop existing database and user to start fresh
DROP DATABASE IF EXISTS school_supplies_db;
DROP USER IF EXISTS 'school_supplies_user'@'localhost';

-- Create the database
CREATE DATABASE school_supplies_db;

-- Create the user with proper password
CREATE USER 'school_supplies_user'@'localhost' IDENTIFIED BY '1234';

-- Grant all privileges on the database to the user
GRANT ALL PRIVILEGES ON school_supplies_db.* TO 'school_supplies_user'@'localhost';

-- Flush privileges to apply changes
FLUSH PRIVILEGES;

-- Switch to the database
USE school_supplies_db;

-- Create tables
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL UNIQUE,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

CREATE TABLE IF NOT EXISTS items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    category_id INT,
    quantity INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(10,2) NOT NULL,
    supplier_id INT,
    minimum_quantity INT NOT NULL DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    transaction_type ENUM('IN', 'OUT') NOT NULL,
    quantity INT NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (item_id) REFERENCES items(item_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    fullname VARCHAR(100) NOT NULL,
    role VARCHAR(10) NOT NULL
);

-- Insert categories with comprehensive data
INSERT INTO categories (category_name, description) VALUES
('Stationery', 'Writing instruments, paper, and office supplies used for writing and desk work'),
('Art Supplies', 'Materials used for drawing, painting, and other artistic activities'),
('Classroom Equipment', 'Physical equipment used to facilitate teaching and learning in classrooms'),
('Paper Products', 'Various types of paper used for writing, printing, and craft'),
('Writing Instruments', 'Pens, pencils, markers, and other tools for writing and drawing'),
('Organization Supplies', 'Items used to organize and store classroom materials'),
('Teaching Aids', 'Materials that assist teachers in presenting information effectively'),
('Technology', 'Electronic devices and accessories used in educational settings'),
('Craft Supplies', 'Materials used for creative craft activities and projects'),
('Furniture', 'Desks, chairs, and other furniture items for classrooms');

-- Insert suppliers with comprehensive data
INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES
('Office Supplies Co.', 'John Doe', '123-456-7890', 'john@officesupplies.com', '123 Business Ave, Suite 101, Springfield, IL 62701'),
('Art Materials Inc.', 'Jane Smith', '987-654-3210', 'jane@artmaterials.com', '456 Creative Blvd, Portland, OR 97201'),
('Classroom Essentials', 'Robert Johnson', '555-123-4567', 'robert@classroomessentials.com', '789 Education St, Boston, MA 02108'),
('Paper World', 'Emily Davis', '222-333-4444', 'emily@paperworld.com', '101 Tree Lane, Seattle, WA 98101'),
('Tech for Schools', 'Michael Wilson', '444-555-6666', 'michael@techforschools.com', '202 Digital Drive, San Jose, CA 95110'),
('Furniture Plus', 'Sarah Brown', '777-888-9999', 'sarah@furnitureplus.com', '303 Comfort Avenue, Grand Rapids, MI 49503'),
('Craft Corner', 'David Miller', '111-222-3333', 'david@craftcorner.com', '404 Artistic Lane, Santa Fe, NM 87501'),
('Educational Innovations', 'Lisa Garcia', '666-777-8888', 'lisa@eduinnovations.com', '505 Smart Street, Austin, TX 78701'),
('Global School Supplies', 'James Wilson', '999-000-1111', 'james@globalschool.com', '606 International Road, Miami, FL 33101'),
('Discount School Items', 'Karen Thompson', '333-444-5555', 'karen@discountschool.com', '707 Bargain Boulevard, Atlanta, GA 30301');

-- Insert items with comprehensive data
INSERT INTO items (item_name, category_id, quantity, unit_price, supplier_id, minimum_quantity) VALUES
-- Stationery items
('Ballpoint Pens (Blue, pack of 12)', 1, 150, 3.99, 1, 30),
('Notebook (A4, spiral bound, 80 pages)', 1, 75, 2.49, 1, 20),
('Stapler (Medium size)', 1, 25, 6.95, 1, 5),
('Paper Clips (Box of 100)', 1, 50, 1.25, 1, 10),
('Sticky Notes (3x3, pack of 5)', 1, 100, 3.75, 1, 25),

-- Art Supplies
('Watercolor Paint Set (12 colors)', 2, 40, 8.99, 2, 10),
('Drawing Pencils (Set of 6, HB to 6B)', 2, 60, 5.49, 2, 15),
('Acrylic Paint (Set of 10 tubes)', 2, 30, 14.99, 2, 8),
('Sketch Pad (9x12, 50 sheets)', 2, 45, 7.25, 2, 12),
('Paint Brushes (Set of 8, assorted sizes)', 2, 35, 9.99, 2, 10),

-- Classroom Equipment
('Whiteboard (36x48 inches)', 3, 15, 45.99, 3, 3),
('Projection Screen (Portable, 72x72 inches)', 3, 8, 79.99, 3, 2),
('World Globe (12-inch diameter)', 3, 12, 34.50, 3, 3),
('Wall Clock (Battery operated, 12-inch)', 3, 20, 18.75, 3, 5),
('Bulletin Board (Cork, 48x36 inches)', 3, 10, 29.99, 3, 3),

-- Paper Products
('Printer Paper (A4, 500 sheets)', 4, 120, 8.49, 4, 30),
('Construction Paper (Assorted colors, 50 sheets)', 4, 85, 6.99, 4, 20),
('Card Stock (White, 100 sheets)', 4, 65, 9.50, 4, 15),
('Graph Paper (4 squares per inch, 100 sheets)', 4, 70, 5.25, 4, 15),
('Colored Paper (A4, 250 sheets, 5 colors)', 4, 55, 12.75, 4, 15),

-- Writing Instruments
('Mechanical Pencils (0.7mm, pack of 5)', 5, 90, 4.99, 1, 20),
('Whiteboard Markers (4 colors set)', 5, 80, 6.50, 1, 20),
('Highlighters (Assorted colors, pack of 6)', 5, 65, 5.75, 1, 15),
('Permanent Markers (Black, pack of 4)', 5, 70, 4.25, 1, 15),
('Colored Pencils (Set of 24)', 5, 50, 7.99, 2, 12),

-- Organization Supplies
('File Folders (Letter size, pack of 25)', 6, 40, 8.99, 1, 10),
('Binders (1-inch, 3-ring)', 6, 60, 4.50, 1, 15),
('Storage Boxes (Plastic, medium size)', 6, 25, 12.99, 6, 5),
('Bookends (Metal, pair)', 6, 30, 9.75, 6, 8),
('Desktop Organizer (Multi-compartment)', 6, 20, 14.50, 6, 5),

-- Teaching Aids
('Alphabet Flashcards', 7, 35, 8.99, 3, 10),
('World Map (Laminated, 50x32 inches)', 7, 25, 15.99, 3, 5),
('Fraction Manipulatives Kit', 7, 20, 24.50, 8, 5),
('Periodic Table Poster (Laminated)', 7, 30, 11.25, 8, 8),
('Geometric Shapes Set (3D, plastic)', 7, 15, 28.75, 8, 4),

-- Technology
('USB Flash Drives (16GB)', 8, 40, 9.99, 5, 10),
('Headphones (Wired, with microphone)', 8, 30, 19.50, 5, 8),
('Calculator (Scientific)', 8, 45, 12.99, 5, 10),
('Wireless Mouse', 8, 25, 15.75, 5, 5),
('Webcam (720p)', 8, 15, 29.99, 5, 4),

-- Craft Supplies
('Glue Sticks (pack of 5)', 9, 90, 3.99, 7, 20),
('Scissors (Blunt tip, student grade)', 9, 75, 2.50, 7, 15),
('Craft Felt (Assorted colors, 9x12, 25 sheets)', 9, 40, 8.75, 7, 10),
('Pipe Cleaners (Assorted colors, pack of 100)', 9, 60, 5.25, 7, 15),
('Beads (Assorted colors and shapes, 500g)', 9, 25, 11.99, 7, 5),

-- Furniture
('Student Desk (Adjustable height)', 10, 20, 89.99, 6, 5),
('Teacher Chair (Ergonomic)', 10, 10, 129.50, 6, 3),
('Bookshelf (5-shelf, wooden)', 10, 8, 149.99, 6, 2),
('Storage Cabinet (Metal, locking)', 10, 5, 199.75, 6, 2),
('Computer Desk (With keyboard tray)', 10, 12, 109.99, 6, 3);

-- Insert transactions with comprehensive data
INSERT INTO transactions (item_id, transaction_type, quantity, transaction_date, notes) VALUES
-- Stock in transactions
(1, 'IN', 50, '2023-07-01 09:15:00', 'Initial inventory'),
(2, 'IN', 30, '2023-07-01 09:30:00', 'Initial inventory'),
(3, 'IN', 15, '2023-07-01 10:00:00', 'Initial inventory'),
(6, 'IN', 20, '2023-07-02 11:45:00', 'Ordered by Art Department'),
(11, 'IN', 5, '2023-07-03 14:20:00', 'New classroom setup'),
(16, 'IN', 50, '2023-07-05 09:10:00', 'Office supplies restock'),
(21, 'IN', 40, '2023-07-06 10:30:00', 'Teacher requested items'),
(26, 'IN', 25, '2023-07-07 13:40:00', 'Admin office organization'),
(31, 'IN', 15, '2023-07-08 15:15:00', 'Elementary classroom materials'),
(36, 'IN', 20, '2023-07-09 09:45:00', 'Computer lab supplies'),
(41, 'IN', 40, '2023-07-10 11:20:00', 'Art class materials'),
(46, 'IN', 10, '2023-07-12 14:00:00', 'New classroom furniture'),

-- Stock out transactions
(1, 'OUT', 10, '2023-07-15 08:30:00', 'Distributed to English Department'),
(2, 'OUT', 5, '2023-07-15 08:45:00', 'Distributed to English Department'),
(6, 'OUT', 5, '2023-07-16 10:15:00', 'Used for art project'),
(11, 'OUT', 1, '2023-07-17 09:30:00', 'Moved to new classroom'),
(16, 'OUT', 10, '2023-07-18 13:20:00', 'Distributed to admin staff'),
(21, 'OUT', 8, '2023-07-19 14:45:00', 'Given to math teachers'),
(26, 'OUT', 5, '2023-07-20 10:30:00', 'Used for office reorganization'),
(31, 'OUT', 3, '2023-07-21 11:15:00', 'Used for science lesson'),
(36, 'OUT', 5, '2023-07-22 09:20:00', 'Distributed to students'),
(41, 'OUT', 12, '2023-07-23 13:40:00', 'Used in art class project'),
(46, 'OUT', 2, '2023-07-24 15:10:00', 'Moved to classroom 101'),

-- Additional stock in
(3, 'IN', 10, '2023-08-01 09:15:00', 'Monthly restock'),
(7, 'IN', 15, '2023-08-01 09:45:00', 'Monthly restock'),
(12, 'IN', 3, '2023-08-01 10:30:00', 'Special order'),
(17, 'IN', 20, '2023-08-02 11:20:00', 'Paper supply restock'),
(22, 'IN', 30, '2023-08-02 13:15:00', 'Teaching supplies restock'),
(27, 'IN', 15, '2023-08-03 14:30:00', 'Office organization'),
(32, 'IN', 10, '2023-08-04 09:10:00', 'Teaching materials'),
(37, 'IN', 5, '2023-08-04 10:45:00', 'Tech department order'),
(42, 'IN', 25, '2023-08-05 13:20:00', 'Art class supplies'),
(47, 'IN', 3, '2023-08-06 15:10:00', 'New furniture'),

-- Additional stock out
(4, 'OUT', 8, '2023-08-10 08:45:00', 'Admin office use'),
(8, 'OUT', 6, '2023-08-10 09:30:00', 'Art class project'),
(13, 'OUT', 2, '2023-08-11 10:15:00', 'Geography classroom'),
(18, 'OUT', 15, '2023-08-11 13:20:00', 'Printing needs'),
(23, 'OUT', 10, '2023-08-12 14:45:00', 'Teacher distribution'),
(28, 'OUT', 5, '2023-08-13 09:30:00', 'Library organization'),
(33, 'OUT', 4, '2023-08-14 11:15:00', 'History classroom'),
(38, 'OUT', 3, '2023-08-15 13:40:00', 'Computer lab'),
(43, 'OUT', 8, '2023-08-16 14:20:00', 'Elementary art project'),
(48, 'OUT', 1, '2023-08-17 15:10:00', 'Teacher office');

-- Insert default users
INSERT INTO users (username, password, fullname, role) VALUES
('admin', 'admin123', 'System Administrator', 'ADMIN'),
('staff1', 'staff123', 'John Staff', 'STAFF'),
('manager1', 'manager123', 'Jane Manager', 'ADMIN'),
('teacher1', 'teacher123', 'Robert Teacher', 'STAFF'),
('inventory1', 'inventory123', 'Emily Inventory', 'STAFF');

-- Flush privileges one final time
FLUSH PRIVILEGES; 