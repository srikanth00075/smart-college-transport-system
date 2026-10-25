package service;

import model.AttendanceRecord;
import model.Complaint;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * CS23304 - Unit 4: File Handling & Object Serialization
 * Handles:
 * 1. Text File I/O: Exporting attendance logs & complaints to CSV/TXT reports.
 * 2. Logging: Appending real-time system events to an audit log file.
 * 3. Serialization: Saving and restoring full system state snapshot as binary objects.
 */
public class DataPersistenceService {
    private static final String AUDIT_LOG_FILE = "transport_system.log";

    /**
     * Unit 4: File Handling - Writes attendance records to CSV.
     */
    public static boolean exportAttendanceToCSV(File file, List<AttendanceRecord> records) {
        if (file == null || records == null) return false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write CSV Header
            writer.write("Attendance_ID,Date,Time,Student_ID,Student_Name,Bus_ID,Route_ID,Trip_Type,Status");
            writer.newLine();

            // Write Records
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");

            for (AttendanceRecord rec : records) {
                String line = String.format("%s,%s,%s,%s,\"%s\",%s,%d,%s,%s",
                        rec.getAttendanceId(),
                        rec.getDate().format(dateFmt),
                        rec.getTime().format(timeFmt),
                        rec.getStudentId(),
                        rec.getStudentName(),
                        rec.getBusId(),
                        rec.getRouteId(),
                        rec.getTripType(),
                        rec.getStatus());
                writer.write(line);
                writer.newLine();
            }
            logEvent("Exported " + records.size() + " attendance records to " + file.getName());
            return true;
        } catch (IOException e) {
            System.err.println("[File Handling Error] Failed to export attendance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Unit 4: File Handling - Exports complaints log to text file.
     */
    public static boolean exportComplaintsReport(File file, List<Complaint> complaints) {
        if (file == null || complaints == null) return false;

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("================================================================================");
            writer.println("                   COLLEGE TRANSPORT COMPLAINT & GRIEVANCE REPORT               ");
            writer.println("                   Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")));
            writer.println("================================================================================");
            writer.println();

            for (Complaint c : complaints) {
                writer.println("Complaint ID : " + c.getComplaintId());
                writer.println("Student      : " + c.getStudentName() + " (" + c.getStudentId() + ")");
                writer.println("Bus Number   : " + c.getBusNumber());
                writer.println("Category     : " + c.getCategory());
                writer.println("Date         : " + c.getSubmissionDate());
                writer.println("Status       : " + c.getStatus());
                writer.println("Description  : " + c.getDescription());
                writer.println("Admin Remarks: " + c.getAdminRemarks());
                writer.println("--------------------------------------------------------------------------------");
            }
            logEvent("Exported " + complaints.size() + " complaints report to " + file.getName());
            return true;
        } catch (IOException e) {
            System.err.println("[File Handling Error] Failed to export complaints: " + e.getMessage());
            return false;
        }
    }

    /**
     * Unit 4: File Handling (Append Mode) - Appends audit events to transport_system.log.
     */
    public static synchronized void logEvent(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = String.format("[%s] %s%n", timestamp, message);

        try (FileWriter fw = new FileWriter(AUDIT_LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(entry);
        } catch (IOException e) {
            System.err.println("[Audit Logger Error] " + e.getMessage());
        }
    }

    /**
     * Unit 4: Serialization - Saves any Serializable object to a binary file.
     */
    public static boolean saveObjectSnapshot(File file, Serializable object) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(object);
            oos.flush();
            logEvent("Serialized snapshot saved to " + file.getName());
            return true;
        } catch (IOException e) {
            System.err.println("[Serialization Error] Could not save snapshot: " + e.getMessage());
            return false;
        }
    }

    /**
     * Unit 4: Deserialization - Reads a Serializable object back from a binary file.
     */
    public static Object loadObjectSnapshot(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            logEvent("Restored snapshot from " + file.getName());
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Deserialization Error] Could not restore snapshot: " + e.getMessage());
            return null;
        }
    }
}
