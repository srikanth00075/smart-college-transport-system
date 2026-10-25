package service;

import database.DBConnection;
import model.AttendanceRecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CS23304 Units Covered:
 * - Unit 1: Date/Time API (LocalDate, LocalTime)
 * - Unit 4: Collections Framework (ArrayList, List), Streams & Lambda Expressions
 * - Unit 5: JDBC Integration for attendance persistence
 */
public class AttendanceService {
    private List<AttendanceRecord> attendanceRecords;

    public AttendanceService() {
        this.attendanceRecords = new ArrayList<>();
        seedDefaultAttendance();
    }

    private void seedDefaultAttendance() {
        // Sample records for past days
        attendanceRecords.add(new AttendanceRecord("ATT-1001", "USR-STU-1", "Aarav Mehta", "BUS-101", 1,
                LocalDate.now().minusDays(2), LocalTime.of(7, 35), "Morning Pickup", "Boarded"));
        attendanceRecords.add(new AttendanceRecord("ATT-1002", "USR-STU-1", "Aarav Mehta", "BUS-101", 1,
                LocalDate.now().minusDays(2), LocalTime.of(16, 50), "Evening Drop", "Boarded"));
        attendanceRecords.add(new AttendanceRecord("ATT-1003", "USR-STU-1", "Aarav Mehta", "BUS-101", 1,
                LocalDate.now().minusDays(1), LocalTime.of(7, 38), "Morning Pickup", "Boarded"));
        attendanceRecords.add(new AttendanceRecord("ATT-1004", "USR-STU-2", "Diya Verma", "BUS-101", 1,
                LocalDate.now().minusDays(1), LocalTime.of(7, 45), "Morning Pickup", "Boarded"));
    }

    public synchronized AttendanceRecord recordAttendance(String studentId, String studentName,
                                                          String busId, int routeId,
                                                          String tripType, String status) {
        AttendanceRecord record = new AttendanceRecord(studentId, studentName, busId, routeId, tripType, status);
        attendanceRecords.add(record);

        // Unit 5: Persist via JDBC
        if (DBConnection.isDatabaseAvailable()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "INSERT INTO attendance (attendance_id, student_id, student_name, bus_id, route_id, date, time, trip_type, status) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setString(1, record.getAttendanceId());
                        pst.setString(2, record.getStudentId());
                        pst.setString(3, record.getStudentName());
                        pst.setString(4, record.getBusId());
                        pst.setInt(5, record.getRouteId());
                        pst.setDate(6, Date.valueOf(record.getDate()));
                        pst.setTime(7, Time.valueOf(record.getTime()));
                        pst.setString(8, record.getTripType());
                        pst.setString(9, record.getStatus());
                        pst.executeUpdate();
                    }
                } catch (Exception e) {
                    System.err.println("[JDBC Attendance Record Error] " + e.getMessage());
                }
            }
        }
        return record;
    }

    // Unit 4: Lambda Expression & Stream Filtering
    public List<AttendanceRecord> getAttendanceForStudent(String studentId) {
        return attendanceRecords.stream()
                .filter(rec -> rec.getStudentId().equalsIgnoreCase(studentId))
                .collect(Collectors.toList());
    }

    public List<AttendanceRecord> getAttendanceForBus(String busId, LocalDate date) {
        return attendanceRecords.stream()
                .filter(rec -> rec.getBusId().equalsIgnoreCase(busId) &&
                        (date == null || rec.getDate().equals(date)))
                .collect(Collectors.toList());
    }

    /**
     * Calculates transport attendance rate percentage using Streams.
     */
    public double calculateAttendancePercentage(String studentId) {
        List<AttendanceRecord> list = getAttendanceForStudent(studentId);
        if (list.isEmpty()) return 100.0; // Default when fresh
        long attended = list.stream().filter(r -> "Boarded".equalsIgnoreCase(r.getStatus())).count();
        return (double) attended / list.size() * 100.0;
    }

    public List<AttendanceRecord> getAllAttendanceRecords() {
        return new ArrayList<>(attendanceRecords);
    }
}
