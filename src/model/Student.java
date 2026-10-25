package model;

/**
 * CS23304 Units Covered:
 * - Unit 1: Constructors, 'this' keyword, super keyword
 * - Unit 2: Inheritance (Student extends User), Interfaces (implements Trackable), Method Overriding
 *
 * Represents an enrolled college student utilizing the transport system.
 */
public class Student extends User implements Trackable {
    private static final long serialVersionUID = 1L;

    private String rollNumber;
    private String department;
    private int year;
    private String assignedBusId;
    private String pickupStop;
    private String boardingStatus; // "Not Boarded", "On Board", "Reached Campus"

    // Default Constructor
    public Student() {
        super();
        this.boardingStatus = "Not Boarded";
    }

    // Parameterized Constructor using super and this
    public Student(String name, String email, String password, String contactNumber,
                   String rollNumber, String department, int year,
                   String assignedBusId, String pickupStop) {
        super(name, email, password, contactNumber);
        this.rollNumber = rollNumber;
        this.department = department;
        this.year = year;
        this.assignedBusId = assignedBusId;
        this.pickupStop = pickupStop;
        this.boardingStatus = "Not Boarded";
    }

    // Explicit constructor with ID
    public Student(String id, String name, String email, String password, String contactNumber,
                   String rollNumber, String department, int year,
                   String assignedBusId, String pickupStop) {
        super(id, name, email, password, contactNumber);
        this.rollNumber = rollNumber;
        this.department = department;
        this.year = year;
        this.assignedBusId = assignedBusId;
        this.pickupStop = pickupStop;
        this.boardingStatus = "Not Boarded";
    }

    // Unit 2: Method Overriding
    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String displayDashboardInfo() {
        return String.format("=== Student Profile ===\n" +
                        "Name: %s\nRoll No: %s\nDept: %s (Year %d)\n" +
                        "Assigned Bus: %s\nPickup Stop: %s\nStatus: %s",
                name, rollNumber, department, year,
                (assignedBusId != null && !assignedBusId.isEmpty() ? assignedBusId : "None (Not Enrolled)"),
                (pickupStop != null && !pickupStop.isEmpty() ? pickupStop : "Not Selected"),
                boardingStatus);
    }

    // Unit 2: Trackable interface implementation
    @Override
    public String getCurrentLocation() {
        return pickupStop != null ? pickupStop : "Home / Unknown";
    }

    @Override
    public void updateLocation(String newLocation) {
        this.pickupStop = newLocation;
    }

    @Override
    public String getStatus() {
        return boardingStatus;
    }

    // Getters and Setters
    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getAssignedBusId() {
        return assignedBusId;
    }

    public void setAssignedBusId(String assignedBusId) {
        this.assignedBusId = assignedBusId;
    }

    public String getPickupStop() {
        return pickupStop;
    }

    public void setPickupStop(String pickupStop) {
        this.pickupStop = pickupStop;
    }

    public String getBoardingStatus() {
        return boardingStatus;
    }

    public void setBoardingStatus(String boardingStatus) {
        this.boardingStatus = boardingStatus;
    }
}
