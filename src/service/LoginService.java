package service;

import database.DBConnection;
import exception.InvalidLoginException;
import model.Admin;
import model.Driver;
import model.Student;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CS23304 Units Covered:
 * - Unit 2: Abstract Class & Polymorphism (User reference holding Student/Driver/Admin)
 * - Unit 3: Exception Handling (throws InvalidLoginException)
 * - Unit 4: Collections Framework (Map, HashMap, List, ArrayList), Lambda Expressions
 * - Unit 5: JDBC (Authenticates & registers against MySQL when available)
 */
public class LoginService {
    // Unit 4: HashMap for high-performance in-memory credential lookups
    private Map<String, User> userRegistry;

    public LoginService() {
        this.userRegistry = new HashMap<>();
        seedDefaultUsers();
    }

    /**
     * Seeds initial test credentials for students, drivers, and administrators.
     */
    private void seedDefaultUsers() {
        // Admin user
        Admin admin = new Admin("USR-ADM-1", "Prof. R. Sharma (Admin)", "admin@college.edu", "admin123", "9876543210",
                "Transport Directorate", "Admin Block 102");
        userRegistry.put(admin.getEmail().toLowerCase(), admin);

        // Drivers
        Driver driver1 = new Driver("USR-DRV-1", "Ramesh Kumar", "ramesh.driver@college.edu", "driver123", "9876500001",
                "DL-MH-2015-8849", 8, "BUS-101", 1, "Full Day");
        Driver driver2 = new Driver("USR-DRV-2", "Suresh Patel", "suresh.driver@college.edu", "driver123", "9876500002",
                "DL-MH-2012-3321", 11, "BUS-102", 2, "Full Day");
        userRegistry.put(driver1.getEmail().toLowerCase(), driver1);
        userRegistry.put(driver2.getEmail().toLowerCase(), driver2);

        // Students
        Student student1 = new Student("USR-STU-1", "Aarav Mehta", "aarav.student@college.edu", "student123", "9811122233",
                "23CS101", "Computer Science", 2, "BUS-101", "City Center Mall");
        Student student2 = new Student("USR-STU-2", "Diya Verma", "diya.student@college.edu", "student123", "9822233344",
                "23IT205", "Information Technology", 2, "BUS-101", "Tech Park Crossing");
        Student student3 = new Student("USR-STU-3", "Rohan Gupta", "rohan.student@college.edu", "student123", "9833344455",
                "23EC304", "Electronics & Comm.", 3, null, null);

        userRegistry.put(student1.getEmail().toLowerCase(), student1);
        userRegistry.put(student2.getEmail().toLowerCase(), student2);
        userRegistry.put(student3.getEmail().toLowerCase(), student3);
    }

    /**
     * Authenticates a user given email, password, and chosen role.
     * Throws InvalidLoginException on failure.
     */
    public User authenticate(String email, String password, String expectedRole) throws InvalidLoginException {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new InvalidLoginException("Email and password fields cannot be empty.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        // 1. Check MySQL if database connection is available
        if (DBConnection.isDatabaseAvailable()) {
            User dbUser = authenticateFromDatabase(normalizedEmail, password, expectedRole);
            if (dbUser != null) {
                userRegistry.put(normalizedEmail, dbUser);
                return dbUser;
            }
        }

        // 2. Fallback to In-Memory Registry
        User user = userRegistry.get(normalizedEmail);
        if (user == null) {
            throw new InvalidLoginException("No registered account found for email: " + email);
        }

        if (!user.authenticate(password)) {
            throw new InvalidLoginException("Incorrect password for account: " + email);
        }

        if (expectedRole != null && !expectedRole.equalsIgnoreCase("Any") &&
                !user.getRole().equalsIgnoreCase(expectedRole.trim())) {
            throw new InvalidLoginException(String.format("Account role is '%s', but login requested as '%s'.",
                    user.getRole(), expectedRole));
        }

        return user;
    }

    /**
     * Unit 5: JDBC Authentication against MySQL
     */
    private User authenticateFromDatabase(String email, String password, String role) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        String sql = "SELECT * FROM users WHERE LOWER(email) = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String contact = rs.getString("contact_number");

                    if ("Student".equalsIgnoreCase(role)) {
                        return new Student(id, name, email, password, contact, "23CS" + id.substring(Math.max(0, id.length() - 3)), "CSE", 2, null, null);
                    } else if ("Driver".equalsIgnoreCase(role)) {
                        return new Driver(id, name, email, password, contact, "LIC-" + id, 5, null, 1, "Full Day");
                    } else {
                        return new Admin(id, name, email, password, contact, "Transport", "Admin Block");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[JDBC Login Query Error] " + e.getMessage());
        }
        return null;
    }

    /**
     * Registers a new student account.
     */
    public synchronized Student registerStudent(String name, String email, String password, String contact,
                                                String rollNumber, String department, int year)
            throws IllegalArgumentException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Student email cannot be empty.");
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (userRegistry.containsKey(normalizedEmail)) {
            throw new IllegalArgumentException("An account with email '" + email + "' already exists.");
        }

        Student student = new Student(name, email, password, contact, rollNumber, department, year, null, null);
        userRegistry.put(normalizedEmail, student);

        // Also persist to DB if available
        if (DBConnection.isDatabaseAvailable()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String userSql = "INSERT INTO users (id, name, email, password, contact_number, role) VALUES (?, ?, ?, ?, ?, 'Student')";
                    try (PreparedStatement pst = conn.prepareStatement(userSql)) {
                        pst.setString(1, student.getId());
                        pst.setString(2, student.getName());
                        pst.setString(3, student.getEmail());
                        pst.setString(4, student.getPassword());
                        pst.setString(5, student.getContactNumber());
                        pst.executeUpdate();
                    }
                    String stuSql = "INSERT INTO students (student_id, roll_number, department, year, boarding_status) VALUES (?, ?, ?, ?, 'Not Boarded')";
                    try (PreparedStatement pst = conn.prepareStatement(stuSql)) {
                        pst.setString(1, student.getId());
                        pst.setString(2, student.getRollNumber());
                        pst.setString(3, student.getDepartment());
                        pst.setInt(4, student.getYear());
                        pst.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.err.println("[JDBC Student Insert Error] " + e.getMessage());
                }
            }
        }
        return student;
    }

    // Unit 4: Lambda Expression filtering
    public List<Student> getAllStudents() {
        return userRegistry.values().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList());
    }

    public List<Driver> getAllDrivers() {
        return userRegistry.values().stream()
                .filter(u -> u instanceof Driver)
                .map(u -> (Driver) u)
                .collect(Collectors.toList());
    }

    public Map<String, User> getUserRegistry() {
        return userRegistry;
    }
}
