package service;

import database.DBConnection;
import model.Complaint;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CS23304 Units Covered:
 * - Unit 4: Collections Framework (ArrayList, List), Streams & Lambdas
 * - Unit 5: JDBC Integration
 */
public class ComplaintService {
    private List<Complaint> complaints;

    public ComplaintService() {
        this.complaints = new ArrayList<>();
        seedDefaultComplaints();
    }

    private void seedDefaultComplaints() {
        Complaint c1 = new Complaint("CMP-501", "USR-STU-1", "Aarav Mehta", "KA-01-EXP-101",
                "Delay", "Bus arrived 25 minutes late at City Center stop due to driver delay.",
                LocalDate.now().minusDays(1), "In Review", "Checking GPS log with driver Ramesh.");
        Complaint c2 = new Complaint("CMP-502", "USR-STU-2", "Diya Verma", "KA-01-EXP-102",
                "Overcrowding", "Bus KA-01-EXP-102 has students standing near the entry door.",
                LocalDate.now().minusDays(3), "Resolved", "Additional backup bus KA-01-EXP-104 assigned to Route 1.");

        complaints.add(c1);
        complaints.add(c2);
    }

    public synchronized Complaint submitComplaint(String studentId, String studentName, String busNumber,
                                                  String category, String description) {
        Complaint complaint = new Complaint(studentId, studentName, busNumber, category, description);
        complaints.add(0, complaint);

        // JDBC persist
        if (DBConnection.isDatabaseAvailable()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "INSERT INTO complaints (complaint_id, student_id, student_name, bus_number, category, description, date, status, admin_remarks) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setString(1, complaint.getComplaintId());
                        pst.setString(2, complaint.getStudentId());
                        pst.setString(3, complaint.getStudentName());
                        pst.setString(4, complaint.getBusNumber());
                        pst.setString(5, complaint.getCategory());
                        pst.setString(6, complaint.getDescription());
                        pst.setDate(7, Date.valueOf(complaint.getSubmissionDate()));
                        pst.setString(8, complaint.getStatus());
                        pst.setString(9, complaint.getAdminRemarks());
                        pst.executeUpdate();
                    }
                } catch (Exception e) {
                    System.err.println("[JDBC Complaint Insert Error] " + e.getMessage());
                }
            }
        }
        return complaint;
    }

    public synchronized boolean updateComplaintStatus(String complaintId, String newStatus, String remarks) {
        for (Complaint c : complaints) {
            if (c.getComplaintId().equalsIgnoreCase(complaintId)) {
                c.setStatus(newStatus);
                c.setAdminRemarks(remarks);

                if (DBConnection.isDatabaseAvailable()) {
                    Connection conn = DBConnection.getConnection();
                    if (conn != null) {
                        try {
                            String sql = "UPDATE complaints SET status = ?, admin_remarks = ? WHERE complaint_id = ?";
                            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                                pst.setString(1, newStatus);
                                pst.setString(2, remarks);
                                pst.setString(3, complaintId);
                                pst.executeUpdate();
                            }
                        } catch (Exception ignored) {}
                    }
                }
                return true;
            }
        }
        return false;
    }

    // Unit 4: Stream filtering
    public List<Complaint> getComplaintsForStudent(String studentId) {
        return complaints.stream()
                .filter(c -> c.getStudentId().equalsIgnoreCase(studentId))
                .collect(Collectors.toList());
    }

    public List<Complaint> getAllComplaints() {
        return new ArrayList<>(complaints);
    }
}
