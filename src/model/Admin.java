package model;

/**
 * CS23304 Units Covered:
 * - Unit 2: Inheritance (Admin extends User), Method Overriding
 *
 * Represents a College Transport Department Administrator.
 */
public class Admin extends User {
    private static final long serialVersionUID = 1L;

    private String departmentHead;
    private String officeRoom;

    public Admin() {
        super();
        this.departmentHead = "Transport Directorate";
        this.officeRoom = "Admin Block 102";
    }

    public Admin(String name, String email, String password, String contactNumber,
                 String departmentHead, String officeRoom) {
        super(name, email, password, contactNumber);
        this.departmentHead = departmentHead;
        this.officeRoom = officeRoom;
    }

    public Admin(String id, String name, String email, String password, String contactNumber,
                 String departmentHead, String officeRoom) {
        super(id, name, email, password, contactNumber);
        this.departmentHead = departmentHead;
        this.officeRoom = officeRoom;
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public String displayDashboardInfo() {
        return String.format("=== Transport Admin Control Panel ===\n" +
                        "Administrator: %s\nEmail: %s\nDepartment: %s\nOffice: %s",
                name, email, departmentHead, officeRoom);
    }

    public String getDepartmentHead() {
        return departmentHead;
    }

    public void setDepartmentHead(String departmentHead) {
        this.departmentHead = departmentHead;
    }

    public String getOfficeRoom() {
        return officeRoom;
    }

    public void setOfficeRoom(String officeRoom) {
        this.officeRoom = officeRoom;
    }
}
