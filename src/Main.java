import database.DBConnection;
import gui.LoginFrame;
import service.*;

import javax.swing.*;

/**
 * CS23304 - Advanced Java Programming
 * Course Project: Smart College Transport Management System
 *
 * This system brings together all 5 Units of Java:
 * - Unit 1: Classes & Objects, Constructors, 'this' & 'static' keywords, Date/Time API
 * - Unit 2: Inheritance, Abstract Classes, Interfaces, Method Overloading & Overriding, Packages
 * - Unit 3: Exception Handling (try-catch-finally, throws, user-defined BusFullException), Multithreading Daemon
 * - Unit 4: File Handling (CSV/TXT export, Audit logging), Serialization, Collections (ArrayList, HashMap, HashSet), Lambdas & Streams
 * - Unit 5: JDBC (MySQL schema & queries), Swing GUI (Forms, Tables, Tabbed Panes, Dialogs, Event Handling)
 *
 * @author CS23304 Student Development Team
 */
public class Main {
    public static void main(String[] args) {
        // Configure Native System Look and Feel for clean modern Swing appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        printBanner();

        // 1. Initialize Service Layer
        BusService busService = new BusService();
        RouteService routeService = new RouteService();
        LoginService loginService = new LoginService();
        AttendanceService attendanceService = new AttendanceService();
        ComplaintService complaintService = new ComplaintService();

        // 2. Unit 3: Start Background Multithreaded Daemon
        NotificationService notificationService = new NotificationService(busService);
        notificationService.start();

        // 3. Unit 4: Log application bootstrap to audit log file
        DataPersistenceService.logEvent("Smart College Transport System initialized successfully.");

        // 4. Unit 5: Launch Swing GUI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(
                    loginService,
                    busService,
                    routeService,
                    attendanceService,
                    complaintService,
                    notificationService
            );
            loginFrame.setVisible(true);
        });
    }

    private static void printBanner() {
        System.out.println("==========================================================================");
        System.out.println("   [BUS] SMART COLLEGE TRANSPORT MANAGEMENT SYSTEM (CS23304 JAVA PROJECT) ");
        System.out.println("==========================================================================");
        System.out.println(" [Unit 1] Classes, Objects, Static Members & Java Date/Time API : LOADED");
        System.out.println(" [Unit 2] Inheritance, Abstract User, Interfaces & Overloading   : LOADED");
        System.out.println(" [Unit 3] User-defined Exceptions & Multithreaded Daemon Service : ACTIVE");
        System.out.println(" [Unit 4] Collections (Map/Set/List), Lambdas & Serialization    : READY");
        System.out.println(" [Unit 5] Swing GUI Components & MySQL JDBC Connector Check      : READY");
        System.out.println("==========================================================================");
        System.out.println(" Database Status: " + (DBConnection.isDatabaseAvailable() ? "MySQL Connected" : "Local Standalone Mode (High-Performance In-Memory / File Persist)"));
        System.out.println(" Launching Graphical User Interface...\n");
    }
}
