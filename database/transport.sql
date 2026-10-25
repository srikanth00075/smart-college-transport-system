-- ========================================================
-- CS23304: Smart College Transport Management System
-- Database Schema Script: transport.sql
-- ========================================================

CREATE DATABASE IF NOT EXISTS smart_transport_db;
USE smart_transport_db;

-- 1. Table: Users (Base table for Student, Driver, Admin)
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20),
    role ENUM('Student', 'Driver', 'Admin') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Table: Students (Subclass entity)
CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(50) PRIMARY KEY,
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(50),
    year INT,
    assigned_bus_id VARCHAR(50),
    pickup_stop VARCHAR(100),
    boarding_status VARCHAR(50) DEFAULT 'Not Boarded',
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Table: Drivers (Subclass entity)
CREATE TABLE IF NOT EXISTS drivers (
    driver_id VARCHAR(50) PRIMARY KEY,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    experience_years INT,
    assigned_bus_id VARCHAR(50),
    assigned_route_id INT,
    shift VARCHAR(50) DEFAULT 'Full Day',
    FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Table: Routes
CREATE TABLE IF NOT EXISTS routes (
    route_id INT PRIMARY KEY,
    route_name VARCHAR(100) NOT NULL,
    starting_point VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    morning_start_time TIME NOT NULL,
    evening_return_time TIME NOT NULL
);

-- 5. Table: Route Stops
CREATE TABLE IF NOT EXISTS route_stops (
    stop_id INT AUTO_INCREMENT PRIMARY KEY,
    route_id INT NOT NULL,
    stop_name VARCHAR(100) NOT NULL,
    stop_sequence INT NOT NULL,
    FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE
);

-- 6. Table: Buses
CREATE TABLE IF NOT EXISTS buses (
    bus_id VARCHAR(50) PRIMARY KEY,
    bus_number VARCHAR(50) NOT NULL UNIQUE,
    capacity INT NOT NULL DEFAULT 50,
    route_id INT,
    driver_id VARCHAR(50),
    current_location VARCHAR(100) DEFAULT 'Depot',
    status VARCHAR(50) DEFAULT 'On Time',
    FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE SET NULL,
    FOREIGN KEY (driver_id) REFERENCES drivers(driver_id) ON DELETE SET NULL
);

-- 7. Table: Bus Allocations / Seat Registrations
CREATE TABLE IF NOT EXISTS bus_registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    bus_id VARCHAR(50) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_student_bus (student_id, bus_id),
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (bus_id) REFERENCES buses(bus_id) ON DELETE CASCADE
);

-- 8. Table: Attendance
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id VARCHAR(50) PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    bus_id VARCHAR(50) NOT NULL,
    route_id INT,
    date DATE NOT NULL,
    time TIME NOT NULL,
    trip_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 9. Table: Complaints
CREATE TABLE IF NOT EXISTS complaints (
    complaint_id VARCHAR(50) PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    bus_number VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    submission_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'Pending',
    admin_remarks TEXT,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 10. Table: Announcements
CREATE TABLE IF NOT EXISTS announcements (
    announcement_id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    sender VARCHAR(100) NOT NULL,
    target_audience VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================================
-- Sample Initial Data Insertion for Demonstration
-- ========================================================

-- Insert Admin
INSERT INTO users (id, name, email, password, contact_number, role)
VALUES ('USR-ADMIN-1', 'Prof. R. Sharma (Admin)', 'admin@college.edu', 'admin123', '9876543210', 'Admin')
ON DUPLICATE KEY UPDATE name=name;

-- Insert Drivers
INSERT INTO users (id, name, email, password, contact_number, role)
VALUES 
('USR-DRV-1', 'Ramesh Kumar', 'ramesh.driver@college.edu', 'driver123', '9876500001', 'Driver'),
('USR-DRV-2', 'Suresh Patel', 'suresh.driver@college.edu', 'driver123', '9876500002', 'Driver')
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO drivers (driver_id, license_number, experience_years, assigned_bus_id, assigned_route_id, shift)
VALUES 
('USR-DRV-1', 'DL-MH-2015-8849', 8, 'BUS-101', 1, 'Full Day'),
('USR-DRV-2', 'DL-MH-2012-3321', 11, 'BUS-102', 2, 'Full Day')
ON DUPLICATE KEY UPDATE license_number=license_number;

-- Insert Students
INSERT INTO users (id, name, email, password, contact_number, role)
VALUES 
('USR-STU-1', 'Aarav Mehta', 'aarav.student@college.edu', 'student123', '9811122233', 'Student'),
('USR-STU-2', 'Diya Verma', 'diya.student@college.edu', 'student123', '9822233344', 'Student')
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO students (student_id, roll_number, department, year, assigned_bus_id, pickupStop, boarding_status)
VALUES 
('USR-STU-1', '23CS101', 'Computer Science', 2, 'BUS-101', 'City Center Mall', 'Not Boarded'),
('USR-STU-2', '23IT205', 'Information Technology', 2, 'BUS-102', 'Metro Station Gate 2', 'Not Boarded')
ON DUPLICATE KEY UPDATE roll_number=roll_number;

-- Insert Routes
INSERT INTO routes (route_id, route_name, starting_point, destination, morning_start_time, evening_return_time)
VALUES 
(1, 'Route 1 - South Central Line', 'Green Park Colony', 'College Campus', '07:15:00', '16:45:00'),
(2, 'Route 2 - Metro Express', 'Metro Pillar 140', 'College Campus', '07:30:00', '17:00:00'),
(3, 'Route 3 - North Suburb Ring', 'North Hills Plaza', 'College Campus', '07:00:00', '16:30:00')
ON DUPLICATE KEY UPDATE route_name=route_name;

-- Insert Route Stops
INSERT INTO route_stops (route_id, stop_name, stop_sequence) VALUES 
(1, 'Green Park Colony', 1),
(1, 'City Center Mall', 2),
(1, 'Tech Park Crossing', 3),
(1, 'College Campus', 4),
(2, 'Metro Pillar 140', 1),
(2, 'Metro Station Gate 2', 2),
(2, 'Botanical Garden', 3),
(2, 'College Campus', 4);

-- Insert Buses
INSERT INTO buses (bus_id, bus_number, capacity, route_id, driver_id, current_location, status)
VALUES 
('BUS-101', 'KA-01-EXP-101', 50, 1, 'USR-DRV-1', 'City Center Mall', 'On Time'),
('BUS-102', 'KA-01-EXP-102', 50, 2, 'USR-DRV-2', 'Metro Station Gate 2', 'Delayed'),
('BUS-103', 'KA-01-EXP-103', 45, 3, NULL, 'Depot', 'Idle')
ON DUPLICATE KEY UPDATE bus_number=bus_number;
