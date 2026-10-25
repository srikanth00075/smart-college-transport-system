package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CS23304 Units Covered:
 * - Unit 1: Classes & Objects, Constructors, 'this' keyword, 'static' member, Date/Time API
 * - Unit 2: Abstract Class, Method Overriding, Interfaces (Loginable)
 * - Unit 4: Serialization (Serializable)
 *
 * Base abstract class for all users in the transport system.
 */
public abstract class User implements Loginable, Serializable {
    private static final long serialVersionUID = 1L;

    // Unit 1: Static members (Auto-increment ID generator)
    private static int userCounter = 1000;

    // Instance attributes
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected String contactNumber;
    protected LocalDateTime registrationDate;

    // Default Constructor (Unit 1: Constructors)
    public User() {
        this.id = generateNextId();
        this.registrationDate = LocalDateTime.now();
    }

    // Parameterized Constructor using 'this' keyword (Unit 1: Constructors & this keyword)
    public User(String name, String email, String password, String contactNumber) {
        this.id = generateNextId();
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.registrationDate = LocalDateTime.now();
    }

    // Parameterized Constructor with explicit ID (for database / preloaded records)
    public User(String id, String name, String email, String password, String contactNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.registrationDate = LocalDateTime.now();
    }

    // Unit 1: Static synchronized method for thread-safe ID generation
    public static synchronized String generateNextId() {
        return "USR-" + (++userCounter);
    }

    // Unit 2: Abstract method to be overridden by Student, Driver, Admin subclasses
    public abstract String displayDashboardInfo();

    // Unit 2: Implementation of Loginable interface
    @Override
    public boolean authenticate(String inputPassword) {
        if (inputPassword == null || this.password == null) {
            return false;
        }
        return this.password.equals(inputPassword.trim());
    }

    // Unit 2: Method Overriding (Object.toString)
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format("[%s] %s (%s) - Contact: %s (Registered: %s)",
                getRole(), name, email, contactNumber,
                registrationDate != null ? registrationDate.format(formatter) : "N/A");
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
