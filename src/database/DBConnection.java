package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * CS23304 - Unit 5: JDBC (Java Database Connectivity)
 * Manages database connectivity to MySQL server.
 * Provides resilient fallback so the application works seamlessly
 * both in standalone mode and with live MySQL instances.
 */
public class DBConnection {
    // Database credentials and connection URL
    private static final String URL = "jdbc:mysql://localhost:3306/smart_transport_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Default standard local password

    private static Connection connection = null;
    private static boolean dbAvailable = false;

    static {
        try {
            // Unit 5: Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            dbAvailable = true;
            System.out.println("[JDBC] Successfully connected to MySQL database: smart_transport_db");
            initializeDatabaseTables();
        } catch (ClassNotFoundException e) {
            System.out.println("[JDBC Warning] MySQL JDBC Driver not found in classpath. Running with In-Memory / Serialized Data Store.");
            dbAvailable = false;
        } catch (SQLException e) {
            System.out.println("[JDBC Info] MySQL database 'smart_transport_db' not reachable on localhost:3306 (" + e.getMessage() + ").");
            System.out.println("[JDBC Info] Operating in High-Performance Local In-Memory & File persistence mode.");
            dbAvailable = false;
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                if (dbAvailable) {
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                }
            }
        } catch (SQLException e) {
            System.err.println("[JDBC Error] Failed to reconnect: " + e.getMessage());
            dbAvailable = false;
        }
        return connection;
    }

    public static boolean isDatabaseAvailable() {
        return dbAvailable && connection != null;
    }

    /**
     * Initializes tables if MySQL is running.
     */
    private static void initializeDatabaseTables() {
        if (!isDatabaseAvailable()) return;
        try (Statement stmt = connection.createStatement()) {
            String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "contact VARCHAR(20), " +
                    "role VARCHAR(20) NOT NULL" +
                    ")";
            stmt.executeUpdate(createUsers);

            String createBuses = "CREATE TABLE IF NOT EXISTS buses (" +
                    "bus_id VARCHAR(50) PRIMARY KEY, " +
                    "bus_number VARCHAR(50) NOT NULL, " +
                    "capacity INT NOT NULL, " +
                    "route_id INT, " +
                    "driver_id VARCHAR(50), " +
                    "current_location VARCHAR(100), " +
                    "status VARCHAR(50)" +
                    ")";
            stmt.executeUpdate(createBuses);

            String createRoutes = "CREATE TABLE IF NOT EXISTS routes (" +
                    "route_id INT PRIMARY KEY, " +
                    "route_name VARCHAR(100) NOT NULL, " +
                    "start_point VARCHAR(100), " +
                    "destination VARCHAR(100), " +
                    "morning_time VARCHAR(20), " +
                    "evening_time VARCHAR(20)" +
                    ")";
            stmt.executeUpdate(createRoutes);

            String createAttendance = "CREATE TABLE IF NOT EXISTS attendance (" +
                    "attendance_id VARCHAR(50) PRIMARY KEY, " +
                    "student_id VARCHAR(50), " +
                    "student_name VARCHAR(100), " +
                    "bus_id VARCHAR(50), " +
                    "route_id INT, " +
                    "date DATE, " +
                    "time TIME, " +
                    "trip_type VARCHAR(50), " +
                    "status VARCHAR(20)" +
                    ")";
            stmt.executeUpdate(createAttendance);

            String createComplaints = "CREATE TABLE IF NOT EXISTS complaints (" +
                    "complaint_id VARCHAR(50) PRIMARY KEY, " +
                    "student_id VARCHAR(50), " +
                    "student_name VARCHAR(100), " +
                    "bus_number VARCHAR(50), " +
                    "category VARCHAR(50), " +
                    "description TEXT, " +
                    "date DATE, " +
                    "status VARCHAR(50), " +
                    "admin_remarks TEXT" +
                    ")";
            stmt.executeUpdate(createComplaints);

            System.out.println("[JDBC] Database schemas verified and ready.");
        } catch (SQLException e) {
            System.err.println("[JDBC Table Init Warning] " + e.getMessage());
        }
    }
}
