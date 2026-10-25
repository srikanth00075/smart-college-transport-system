package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * CS23304 Units Covered:
 * - Unit 1: Classes & Objects, Date/Time API (LocalDate, LocalTime)
 * - Unit 4: Serialization
 *
 * Represents a student's daily transport boarding attendance.
 */
public class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int recordCounter = 1000;

    private String attendanceId;
    private String studentId;
    private String studentName;
    private String busId;
    private int routeId;
    private LocalDate date;
    private LocalTime time;
    private String tripType; // "Morning Pickup" or "Evening Drop"
    private String status;   // "Boarded" or "Absent"

    public AttendanceRecord() {
        this.attendanceId = "ATT-" + (++recordCounter);
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.tripType = "Morning Pickup";
        this.status = "Boarded";
    }

    public AttendanceRecord(String studentId, String studentName, String busId,
                            int routeId, String tripType, String status) {
        this.attendanceId = "ATT-" + (++recordCounter);
        this.studentId = studentId;
        this.studentName = studentName;
        this.busId = busId;
        this.routeId = routeId;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.tripType = tripType;
        this.status = status;
    }

    public AttendanceRecord(String attendanceId, String studentId, String studentName,
                            String busId, int routeId, LocalDate date, LocalTime time,
                            String tripType, String status) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.busId = busId;
        this.routeId = routeId;
        this.date = date;
        this.time = time;
        this.tripType = tripType;
        this.status = status;
    }

    @Override
    public String toString() {
        DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter tFmt = DateTimeFormatter.ofPattern("hh:mm a");
        return String.format("[%s] %s - %s (%s) | %s at %s - Bus: %s",
                attendanceId, date.format(dFmt), studentName, studentId,
                tripType, time.format(tFmt), busId);
    }

    // Getters and Setters
    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
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

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
