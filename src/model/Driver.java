package model;

/**
 * CS23304 Units Covered:
 * - Unit 2: Inheritance (Driver extends User), Method Overriding
 *
 * Represents an authorized transport driver responsible for a bus and route.
 */
public class Driver extends User {
    private static final long serialVersionUID = 1L;

    private String licenseNumber;
    private int experienceYears;
    private String assignedBusId;
    private int assignedRouteId;
    private String shift; // "Morning", "Evening", "Full Day"

    // Default Constructor
    public Driver() {
        super();
        this.shift = "Full Day";
    }

    // Parameterized Constructor
    public Driver(String name, String email, String password, String contactNumber,
                  String licenseNumber, int experienceYears,
                  String assignedBusId, int assignedRouteId, String shift) {
        super(name, email, password, contactNumber);
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
        this.assignedBusId = assignedBusId;
        this.assignedRouteId = assignedRouteId;
        this.shift = shift;
    }

    public Driver(String id, String name, String email, String password, String contactNumber,
                  String licenseNumber, int experienceYears,
                  String assignedBusId, int assignedRouteId, String shift) {
        super(id, name, email, password, contactNumber);
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
        this.assignedBusId = assignedBusId;
        this.assignedRouteId = assignedRouteId;
        this.shift = shift;
    }

    @Override
    public String getRole() {
        return "Driver";
    }

    @Override
    public String displayDashboardInfo() {
        return String.format("=== Driver Dashboard ===\n" +
                        "Name: %s\nLicense: %s (Experience: %d yrs)\n" +
                        "Assigned Bus: %s\nAssigned Route ID: %d\nShift: %s",
                name, licenseNumber, experienceYears,
                (assignedBusId != null ? assignedBusId : "Unassigned"),
                assignedRouteId, shift);
    }

    // Getters and Setters
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getAssignedBusId() {
        return assignedBusId;
    }

    public void setAssignedBusId(String assignedBusId) {
        this.assignedBusId = assignedBusId;
    }

    public int getAssignedRouteId() {
        return assignedRouteId;
    }

    public void setAssignedRouteId(int assignedRouteId) {
        this.assignedRouteId = assignedRouteId;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
