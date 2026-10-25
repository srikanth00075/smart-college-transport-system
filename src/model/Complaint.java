package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * CS23304 Units Covered:
 * - Unit 1: Classes & Objects, Date/Time API (LocalDate)
 * - Unit 4: Serialization
 *
 * Represents an issue or grievance submitted by a student regarding transport.
 */
public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int complaintCounter = 500;

    private String complaintId;
    private String studentId;
    private String studentName;
    private String busNumber;
    private String category;
    private String description;
    private LocalDate submissionDate;
    private String status; // "Pending", "In Review", "Resolved"
    private String adminRemarks;

    public Complaint() {
        this.complaintId = "CMP-" + (++complaintCounter);
        this.submissionDate = LocalDate.now();
        this.status = "Pending";
        this.adminRemarks = "Awaiting transport supervisor review.";
    }

    public Complaint(String studentId, String studentName, String busNumber,
                     String category, String description) {
        this.complaintId = "CMP-" + (++complaintCounter);
        this.studentId = studentId;
        this.studentName = studentName;
        this.busNumber = busNumber;
        this.category = category;
        this.description = description;
        this.submissionDate = LocalDate.now();
        this.status = "Pending";
        this.adminRemarks = "Awaiting transport supervisor review.";
    }

    public Complaint(String complaintId, String studentId, String studentName, String busNumber,
                     String category, String description, LocalDate submissionDate,
                     String status, String adminRemarks) {
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.busNumber = busNumber;
        this.category = category;
        this.description = description;
        this.submissionDate = submissionDate;
        this.status = status;
        this.adminRemarks = adminRemarks;
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        return String.format("[%s] (%s) %s - Bus: %s [%s] Remark: %s",
                complaintId, status, category, busNumber, submissionDate.format(dateFmt), adminRemarks);
    }

    // Getters and Setters
    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }
}
