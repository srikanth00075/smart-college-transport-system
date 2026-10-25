package gui;

import model.*;
import service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CS23304 - Unit 5: AWT / Swing GUI & Event Handling
 * DriverDashboard allows college drivers to:
 * - Review their assigned bus, route stops, and schedule
 * - View registered passengers and record student boarding attendance
 * - Report real-time trip delays or mechanical problems
 */
public class DriverDashboard extends JFrame {
    private final Driver driver;
    private final BusService busService;
    private final RouteService routeService;
    private final AttendanceService attendanceService;
    private final NotificationService notificationService;
    private final LoginService loginService;

    private JTable tblStudents;
    private DefaultTableModel studentTableModel;
    private JComboBox<String> cmbStatusUpdate;
    private JTextField txtLocationUpdate;
    private JLabel lblCurrentBusStatus;

    public DriverDashboard(Driver driver, BusService busService, RouteService routeService,
                           AttendanceService attendanceService, NotificationService notificationService,
                           LoginService loginService) {
        this.driver = driver;
        this.busService = busService;
        this.routeService = routeService;
        this.attendanceService = attendanceService;
        this.notificationService = notificationService;
        this.loginService = loginService;

        setTitle("College Fleet Operations - Driver Console (" + driver.getName() + ")");
        setSize(880, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.COLOR_BG);

        // Header Banner
        JPanel header = UIUtils.createHeaderBanner(
                "Driver Fleet Operations Console",
                "Driver: " + driver.getName() + " | License: " + driver.getLicenseNumber() + " | Shift: " + driver.getShift(),
                "Driver"
        );

        JButton btnLogout = UIUtils.createStyledButton("Logout", UIUtils.COLOR_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> logout());
        header.add(btnLogout, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.FONT_SUBHEADER);

        tabs.addTab("My Route & Schedule", createRouteScheduleTab());
        tabs.addTab("Student Boarding Checklist", createBoardingChecklistTab());
        tabs.addTab("Report Delay / Vehicle Problem", createReportStatusTab());

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ==========================================================
    // Tab 1: Route & Timetable
    // ==========================================================
    private JPanel createRouteScheduleTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new GridLayout(6, 1, 10, 10));

        Bus b = busService.getBusById(driver.getAssignedBusId());
        String busInfo = (b != null) ? b.getBusNumber() + " (" + b.getBusId() + ")" : "Not Assigned";
        String statusInfo = (b != null) ? b.getStatus() : "Idle";

        JLabel lblBus = new JLabel("Assigned Bus: " + busInfo);
        lblBus.setFont(UIUtils.FONT_HEADER);

        lblCurrentBusStatus = new JLabel("Live Operating Status: " + statusInfo);
        lblCurrentBusStatus.setFont(UIUtils.FONT_HEADER);
        lblCurrentBusStatus.setForeground(UIUtils.COLOR_PRIMARY);

        Optional<Route> rOpt = routeService.searchRoute(driver.getAssignedRouteId());
        String routeName = rOpt.map(Route::getRouteName).orElse("Standard City Route");
        String stops = rOpt.map(Route::getStopsFormatted).orElse("Depot -> Campus");
        String mTime = rOpt.map(r -> r.getMorningStartTime().toString()).orElse("07:30 AM");
        String eTime = rOpt.map(r -> r.getEveningReturnTime().toString()).orElse("16:45 PM");

        card.add(lblBus);
        card.add(lblCurrentBusStatus);
        card.add(new JLabel("Assigned Route: " + routeName));
        card.add(new JLabel("Morning Pickup Start: " + mTime + " | Evening Campus Departure: " + eTime));
        card.add(new JLabel("Route Stops Sequence: " + stops));

        panel.add(card, BorderLayout.NORTH);

        JPanel info = UIUtils.createCardPanel();
        info.setLayout(new BorderLayout());
        JTextArea txt = new JTextArea("DRIVER RESPONSIBILITIES & SAFETY PROTOCOLS:\n\n" +
                "1. Adhere strictly to the designated bus route and scheduled pickup stops.\n" +
                "2. Mark student boarding when students board the bus at designated stops.\n" +
                "3. In the event of heavy traffic congestion or breakdown, immediately report via the 'Report Delay' tab so students receive live alerts.");
        txt.setEditable(false);
        txt.setFont(UIUtils.FONT_BODY);
        info.add(txt, BorderLayout.CENTER);

        panel.add(info, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================================
    // Tab 2: Student Boarding Checklist
    // ==========================================================
    private JPanel createBoardingChecklistTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topCard = UIUtils.createCardPanel();
        topCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        JButton btnBoard = UIUtils.createStyledButton("Mark Selected Student Boarded", UIUtils.COLOR_SUCCESS, Color.WHITE);
        btnBoard.addActionListener(e -> markSelectedStudentBoarded());

        JButton btnBoardAll = UIUtils.createStyledButton("Mark All Boarded", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnBoardAll.addActionListener(e -> markAllBoarded());

        topCard.add(btnBoard);
        topCard.add(btnBoardAll);
        panel.add(topCard, BorderLayout.NORTH);

        String[] cols = {"Student ID", "Name", "Roll Number", "Department", "Pickup Stop", "Boarding Status"};
        studentTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblStudents = new JTable(studentTableModel);
        UIUtils.formatTable(tblStudents);

        panel.add(new JScrollPane(tblStudents), BorderLayout.CENTER);
        refreshStudentsList();
        return panel;
    }

    private void refreshStudentsList() {
        if (studentTableModel == null) return;
        studentTableModel.setRowCount(0);
        String assignedBus = driver.getAssignedBusId();

        List<Student> students = loginService.getAllStudents().stream()
                .filter(s -> assignedBus != null && assignedBus.equalsIgnoreCase(s.getAssignedBusId()))
                .collect(Collectors.toList());

        for (Student s : students) {
            studentTableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getRollNumber(),
                    s.getDepartment(),
                    (s.getPickupStop() != null ? s.getPickupStop() : "City Center Mall"),
                    s.getBoardingStatus()
            });
        }
    }

    private void markSelectedStudentBoarded() {
        if (tblStudents == null) return;
        int row = tblStudents.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student from the list first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sId = (String) studentTableModel.getValueAt(row, 0);
        String sName = (String) studentTableModel.getValueAt(row, 1);

        attendanceService.recordAttendance(sId, sName, driver.getAssignedBusId(), driver.getAssignedRouteId(), "Morning Pickup", "Boarded");

        // Update student state
        for (Student s : loginService.getAllStudents()) {
            if (s.getId().equals(sId)) {
                s.setBoardingStatus("On Board (Verified by Driver)");
            }
        }
        refreshStudentsList();
        JOptionPane.showMessageDialog(this, "Marked " + sName + " as Boarded.");
    }

    private void markAllBoarded() {
        for (Student s : loginService.getAllStudents()) {
            if (driver.getAssignedBusId() != null && driver.getAssignedBusId().equalsIgnoreCase(s.getAssignedBusId())) {
                attendanceService.recordAttendance(s.getId(), s.getName(), driver.getAssignedBusId(), driver.getAssignedRouteId(), "Morning Pickup", "Boarded");
                s.setBoardingStatus("On Board (Verified by Driver)");
            }
        }
        refreshStudentsList();
        JOptionPane.showMessageDialog(this, "All assigned passengers marked as Boarded!");
    }

    // ==========================================================
    // Tab 3: Report Delay & Issues
    // ==========================================================
    private JPanel createReportStatusTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new GridLayout(4, 2, 12, 12));

        String[] statuses = {
                "On Time",
                "Delayed by 10 mins (Traffic Congestion)",
                "Delayed by 20 mins (Accident / Roadblock)",
                "Vehicle Breakdown (Flat Tyre / Engine)",
                "Reached College Campus Gate"
        };
        cmbStatusUpdate = new JComboBox<>(statuses);

        txtLocationUpdate = new JTextField("Approaching City Center Mall Crossing");

        JButton btnUpdateStatus = UIUtils.createStyledButton("Broadcast Live Status Update", UIUtils.COLOR_DANGER, Color.WHITE);
        btnUpdateStatus.addActionListener(e -> {
            String newStatus = (String) cmbStatusUpdate.getSelectedItem();
            String newLoc = txtLocationUpdate.getText().trim();

            Bus b = busService.getBusById(driver.getAssignedBusId());
            if (b != null) {
                b.setStatus(newStatus);
                b.updateLocation(newLoc);
                lblCurrentBusStatus.setText("Live Operating Status: " + newStatus);

                // Broadcast alert to all students
                Announcement alert = new Announcement(
                        "Bus Status Update: " + b.getBusNumber(),
                        "Driver " + driver.getName() + " reported: " + newStatus + " at " + newLoc,
                        driver.getName(),
                        "Students"
                );
                notificationService.broadcastAnnouncement(alert);

                JOptionPane.showMessageDialog(this,
                        "Status updated to '" + newStatus + "'!\nLive broadcast sent to all students.",
                        "Status Broadcasted", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        card.add(new JLabel("Select Live Status:")); card.add(cmbStatusUpdate);
        card.add(new JLabel("Current Bus Geolocation/Stop:")); card.add(txtLocationUpdate);
        card.add(new JLabel("")); card.add(btnUpdateStatus);

        panel.add(card, BorderLayout.NORTH);
        return panel;
    }

    private void logout() {
        new LoginFrame(loginService, busService, routeService,
                attendanceService, new ComplaintService(), notificationService).setVisible(true);
        this.dispose();
    }
}
