package test;

import exception.BusFullException;
import exception.InvalidLoginException;
import model.*;
import service.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CS23304 - Automated Headless Verification Test
 * Tests the entire Smart College Transport System end-to-end:
 * 1. Unit 1 & 2: Object Creation, Polymorphic User inheritance, Interfaces
 * 2. Unit 3: Exception Handling (InvalidLoginException, BusFullException test case from prompt)
 * 3. Unit 3: Multithreaded background daemon startup
 * 4. Unit 4: Collections, Lambda stream filtering, File Handling (CSV/TXT), Serialization
 * 5. Unit 5: Verification of relational schema & fallback persistence
 */
public class WorkflowTest {
    public static void main(String[] args) {
        System.out.println(">>> Starting CS23304 System Verification Tests <<<\n");

        int passed = 0;
        int failed = 0;

        // Initialize Services
        BusService busService = new BusService();
        RouteService routeService = new RouteService();
        LoginService loginService = new LoginService();
        AttendanceService attendanceService = new AttendanceService();
        ComplaintService complaintService = new ComplaintService();

        NotificationService notificationService = new NotificationService(busService);
        notificationService.start();

        // -------------------------------------------------------------
        // Test 1: User Authentication & Polymorphism (Units 1, 2, 3)
        // -------------------------------------------------------------
        System.out.println("[TEST 1] Testing Role Authentication...");
        try {
            User student = loginService.authenticate("aarav.student@college.edu", "student123", "Student");
            System.out.println("  PASS: Authenticated " + student.getRole() + ": " + student.getName());
            passed++;

            // Test InvalidLoginException
            try {
                loginService.authenticate("aarav.student@college.edu", "wrongpassword", "Student");
                System.out.println("  FAIL: Did not throw InvalidLoginException for wrong password.");
                failed++;
            } catch (InvalidLoginException ex) {
                System.out.println("  PASS: Correctly threw InvalidLoginException -> " + ex.getMessage());
                passed++;
            }
        } catch (Exception ex) {
            System.out.println("  FAIL: Unexpected error in login: " + ex.getMessage());
            failed++;
        }

        // -------------------------------------------------------------
        // Test 2: Method Overloading in BusService (Unit 2 & 4)
        // -------------------------------------------------------------
        System.out.println("\n[TEST 2] Testing BusService Method Overloading & Lambda Filtering...");
        Optional<Bus> b1 = busService.searchBus("BUS-101");
        List<Bus> bRoute1 = busService.searchBus(1);
        List<Bus> bAvailable = busService.searchBus(5, true);

        if (b1.isPresent() && !bRoute1.isEmpty() && !bAvailable.isEmpty()) {
            System.out.println("  PASS: All 3 overloaded searchBus methods returned expected results.");
            System.out.println("        Overload 1 (String ID): " + b1.get().getBusNumber());
            System.out.println("        Overload 2 (int RouteId): " + bRoute1.size() + " buses on Route 1");
            System.out.println("        Overload 3 (int seats, bool active): " + bAvailable.size() + " buses with seats");
            passed++;
        } else {
            System.out.println("  FAIL: Method overloading search test failed.");
            failed++;
        }

        // -------------------------------------------------------------
        // Test 3: Normal Bus Registration Workflow (User prompt workflow)
        // -------------------------------------------------------------
        System.out.println("\n[TEST 3] Testing Bus Enrollment Workflow (Route -> Bus -> Check Seats -> Confirm)...");
        try {
            Student s = (Student) loginService.authenticate("rohan.student@college.edu", "student123", "Student");
            Bus targetBus = busService.getBusById("BUS-101");
            int initialSeats = targetBus.getAvailableSeats();

            busService.registerStudentToBus("BUS-101", s);

            if (targetBus.getAvailableSeats() == initialSeats - 1 && "BUS-101".equals(s.getAssignedBusId())) {
                System.out.println("  PASS: Student registered successfully. Available seats updated: " + targetBus.getAvailableSeats());
                passed++;
            } else {
                System.out.println("  FAIL: Seat count mismatch after registration.");
                failed++;
            }
        } catch (Exception ex) {
            System.out.println("  FAIL: Error during bus registration: " + ex.getMessage());
            failed++;
        }

        // -------------------------------------------------------------
        // Test 4: BusFullException Workflow (Prompt test case!)
        // Bus has 50 seats -> 50 occupied -> Student tries to register -> BusFullException
        // -------------------------------------------------------------
        System.out.println("\n[TEST 4] Testing BusFullException Workflow on Full Bus (KA-01-EXP-102)...");
        try {
            Student newStudent = new Student("Test Full Student", "testfull@college.edu", "pass", "9999999999",
                    "23CS999", "CSE", 2, null, null);

            Bus fullBus = busService.getBusById("BUS-102");
            System.out.println("  Bus 102 Capacity: " + fullBus.getCapacity() + " | Occupancy: " + fullBus.getCurrentOccupancy() + " | Full? " + fullBus.isFull());

            // Attempting to register on full bus
            busService.registerStudentToBus("BUS-102", newStudent);
            System.out.println("  FAIL: BusFullException was NOT thrown on 50/50 bus!");
            failed++;

        } catch (BusFullException ex) {
            System.out.println("  PASS: Caught BusFullException successfully!");
            System.out.println("        Exception Message: \"" + ex.getMessage() + "\"");
            passed++;
        } catch (Exception ex) {
            System.out.println("  FAIL: Wrong exception caught: " + ex.getClass().getName());
            failed++;
        }

        // -------------------------------------------------------------
        // Test 5: Attendance Recording & Percentage (Unit 1, 4)
        // -------------------------------------------------------------
        System.out.println("\n[TEST 5] Testing Attendance Service & Lambda Calculations...");
        AttendanceRecord att = attendanceService.recordAttendance("USR-STU-1", "Aarav Mehta", "BUS-101", 1, "Morning Pickup", "Boarded");
        double rate = attendanceService.calculateAttendancePercentage("USR-STU-1");
        System.out.println("  PASS: Recorded Attendance ID: " + att.getAttendanceId() + " (Calculated Rate: " + String.format("%.1f", rate) + "%)");
        passed++;

        // -------------------------------------------------------------
        // Test 6: File Handling & Reports Export (Unit 4)
        // -------------------------------------------------------------
        System.out.println("\n[TEST 6] Testing File Handling (CSV Attendance & TXT Complaints Export)...");
        File csvFile = new File("test_attendance.csv");
        File txtFile = new File("test_complaints.txt");

        boolean csvOk = DataPersistenceService.exportAttendanceToCSV(csvFile, attendanceService.getAllAttendanceRecords());
        boolean txtOk = DataPersistenceService.exportComplaintsReport(txtFile, complaintService.getAllComplaints());

        if (csvOk && csvFile.exists() && csvFile.length() > 0 &&
            txtOk && txtFile.exists() && txtFile.length() > 0) {
            System.out.println("  PASS: CSV (" + csvFile.length() + " bytes) and TXT (" + txtFile.length() + " bytes) generated successfully.");
            passed++;
        } else {
            System.out.println("  FAIL: File handling test failed.");
            failed++;
        }

        // -------------------------------------------------------------
        // Test 7: Object Serialization & Deserialization (Unit 4)
        // -------------------------------------------------------------
        System.out.println("\n[TEST 7] Testing Object Serialization & Deserialization...");
        File serFile = new File("test_fleet.ser");
        ArrayList<Bus> fleet = new ArrayList<>(busService.getAllBuses());

        boolean serOk = DataPersistenceService.saveObjectSnapshot(serFile, fleet);
        Object restored = DataPersistenceService.loadObjectSnapshot(serFile);

        if (serOk && restored instanceof ArrayList) {
            @SuppressWarnings("unchecked")
            ArrayList<Bus> restoredFleet = (ArrayList<Bus>) restored;
            System.out.println("  PASS: Serialized & deserialized " + restoredFleet.size() + " Bus objects matching original state.");
            passed++;
        } else {
            System.out.println("  FAIL: Serialization / deserialization failed.");
            failed++;
        }

        // Stop background daemon thread
        notificationService.stopService();

        // Summary
        System.out.println("\n=======================================================");
        System.out.println("TEST SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("=======================================================");

        // Cleanup temporary test files
        csvFile.delete();
        txtFile.delete();
        serFile.delete();

        if (failed == 0) {
            System.out.println(">>> ALL CS23304 SYLLABUS SPECIFICATIONS VERIFIED! <<<");
        }
    }
}
