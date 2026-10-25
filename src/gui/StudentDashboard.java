package gui;

import exception.BusFullException;
import model.*;
import service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * CS23304 - Unit 5: AWT / Swing GUI & Event Handling
 * StudentDashboard provides the primary interface for students to:
 * - View assigned bus, route stops, and real-time timings
 * - Browse routes, inspect seat availability, and enroll (triggers BusFullException if full)
 * - Mark boarding attendance and review attendance history
 * - Submit complaints and track admin resolution
 * - Receive live announcements via background notification thread
 */
public class StudentDashboard extends JFrame {
    private final Student student;
    private final BusService busService;
    private final RouteService routeService;
    private final AttendanceService attendanceService;
    private final ComplaintService complaintService;
    private final NotificationService notificationService;

    // UI Components
    private JLabel lblAssignedBus;
    private JLabel lblCurrentLocation;
    private JLabel lblBusStatus;
    private JLabel lblRouteDetails;
    private JLabel lblAttendancePercent;

    private JComboBox<String> cmbRoutes;
    private JComboBox<String> cmbStops;
    private JTable tblBuses;
    private DefaultTableModel busTableModel;

    private JTable tblAttendance;
    private DefaultTableModel attendanceTableModel;

    private JTable tblComplaints;
    private DefaultTableModel complaintsTableModel;

    private JTextArea txtAnnouncements;

    public StudentDashboard(Student student, BusService busService, RouteService routeService,
                            AttendanceService attendanceService, ComplaintService complaintService,
                            NotificationService notificationService) {
        this.student = student;
        this.busService = busService;
        this.routeService = routeService;
        this.attendanceService = attendanceService;
        this.complaintService = complaintService;
        this.notificationService = notificationService;

        setTitle("Student Transport Portal - " + student.getName() + " (" + student.getRollNumber() + ")");
        setSize(920, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();

        // Unit 3: Hook into Multithreaded Background Service
        notificationService.addListener(event -> SwingUtilities.invokeLater(this::refreshLiveStatus));
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.COLOR_BG);

        // Header Banner with Logout Button
        JPanel header = UIUtils.createHeaderBanner(
                "Smart College Transport System",
                "Logged in as: " + student.getName() + " | Roll: " + student.getRollNumber() + " | Dept: " + student.getDepartment(),
                "Student"
        );

        JButton btnLogout = UIUtils.createStyledButton("Logout", UIUtils.COLOR_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> logout());
        header.add(btnLogout, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Main Tabbed Pane (Unit 5: Swing JTabbedPane)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_SUBHEADER);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("My Bus & Route", createMyBusTab());
        tabbedPane.addTab("Available Routes & Registration", createRouteRegistrationTab());
        tabbedPane.addTab("Transport Attendance", createAttendanceTab());
        tabbedPane.addTab("Submit Complaints", createComplaintsTab());
        tabbedPane.addTab("College Announcements", createAnnouncementsTab());

        root.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ==========================================================
    // Tab 1: My Bus & Route Overview
    // ==========================================================
    private JPanel createMyBusTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new GridLayout(6, 1, 10, 10));

        lblAssignedBus = new JLabel();
        lblCurrentLocation = new JLabel();
        lblBusStatus = new JLabel();
        lblRouteDetails = new JLabel();
        lblAttendancePercent = new JLabel();

        lblAssignedBus.setFont(UIUtils.FONT_HEADER);
        lblCurrentLocation.setFont(UIUtils.FONT_BODY);
        lblBusStatus.setFont(UIUtils.FONT_BODY);
        lblRouteDetails.setFont(UIUtils.FONT_BODY);
        lblAttendancePercent.setFont(UIUtils.FONT_HEADER);
        lblAttendancePercent.setForeground(UIUtils.COLOR_PRIMARY);

        card.add(lblAssignedBus);
        card.add(lblRouteDetails);
        card.add(lblCurrentLocation);
        card.add(lblBusStatus);
        card.add(lblAttendancePercent);

        JButton btnRefresh = UIUtils.createStyledButton("Refresh Live Location", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnRefresh.addActionListener(e -> refreshLiveStatus());
        card.add(btnRefresh);

        panel.add(card, BorderLayout.NORTH);

        // Informative guidance panel
        JPanel infoPanel = UIUtils.createCardPanel();
        infoPanel.setLayout(new BorderLayout());
        JTextArea txtGuide = new JTextArea();
        txtGuide.setEditable(false);
        txtGuide.setFont(UIUtils.FONT_BODY);
        txtGuide.setBackground(Color.WHITE);
        txtGuide.setText("[CS23304 Multithreading Architecture Note]:\n\n" +
                "1. A background Daemon Thread (NotificationService) periodically monitors fleet tracking updates.\n" +
                "2. When your assigned driver reports heavy traffic or a delay, your dashboard status automatically refreshes.\n" +
                "3. You can mark your boarding when you enter the bus at your chosen pickup point.");
        infoPanel.add(txtGuide, BorderLayout.CENTER);

        panel.add(infoPanel, BorderLayout.CENTER);

        refreshLiveStatus();
        return panel;
    }

    // ==========================================================
    // Tab 2: Workflow - Select Route -> Select Bus -> Check Seats -> Confirm
    // ==========================================================
    private JPanel createRouteRegistrationTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Filter Panel
        JPanel filterCard = UIUtils.createCardPanel();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        filterCard.add(new JLabel("Select Route:"));
        cmbRoutes = new JComboBox<>();
        cmbRoutes.setFont(UIUtils.FONT_BODY);
        for (Route r : routeService.getAllRoutes()) {
            cmbRoutes.addItem("Route " + r.getRouteId() + ": " + r.getRouteName());
        }
        cmbRoutes.addActionListener(e -> updateRouteSelection());
        filterCard.add(cmbRoutes);

        filterCard.add(new JLabel("Select Pickup Stop:"));
        cmbStops = new JComboBox<>();
        cmbStops.setFont(UIUtils.FONT_BODY);
        filterCard.add(cmbStops);

        panel.add(filterCard, BorderLayout.NORTH);

        // Center Buses Table
        String[] columns = {"Bus ID", "Bus Number", "Capacity", "Occupied", "Available Seats", "Live Status", "Depot/Stop"};
        busTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblBuses = new JTable(busTableModel);
        UIUtils.formatTable(tblBuses);

        JScrollPane scrollTable = new JScrollPane(tblBuses);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Buses Operating on Selected Route"));
        panel.add(scrollTable, BorderLayout.CENTER);

        // Bottom Registration Action Panel
        JPanel actionPanel = UIUtils.createCardPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JButton btnRegister = UIUtils.createStyledButton("Confirm Seat & Enroll to Bus", UIUtils.COLOR_SUCCESS, Color.WHITE);
        btnRegister.addActionListener(e -> executeBusRegistrationWorkflow());

        actionPanel.add(new JLabel("Select a bus from table above then click:"));
        actionPanel.add(btnRegister);

        panel.add(actionPanel, BorderLayout.SOUTH);

        updateRouteSelection();
        return panel;
    }

    /**
     * Executes the user's explicit workflow:
     * View Routes -> Select Route -> Select Bus -> Check Seat Availability -> Confirm -> Update DB
     * If bus is full: throws BusFullException -> "Sorry! Bus is full."
     */
    private void executeBusRegistrationWorkflow() {
        int selectedRow = tblBuses.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bus from the table first.",
                    "No Bus Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String busId = (String) busTableModel.getValueAt(selectedRow, 0);
        String busNumber = (String) busTableModel.getValueAt(selectedRow, 1);
        String chosenStop = (String) cmbStops.getSelectedItem();

        try {
            // Unit 3: Exception Handling - registerStudentToBus throws BusFullException if full!
            busService.registerStudentToBus(busId, student);
            student.setPickupStop(chosenStop);

            // Success feedback
            JOptionPane.showMessageDialog(this,
                    "Seat Confirmed!\n" +
                            "You are successfully registered to " + busNumber + ".\n" +
                            "Pickup Stop: " + chosenStop + "\n" +
                            "Database & Allocations Updated.",
                    "Registration Confirmed", JOptionPane.INFORMATION_MESSAGE);

            refreshLiveStatus();
            updateRouteSelection();

        } catch (BusFullException ex) {
            // Unit 3: Handling User-defined Exception as specified
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() + "\nPlease choose another bus or contact the Transport Administrator.",
                    "Registration Failed (BusFullException)", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error during registration: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRouteSelection() {
        int index = cmbRoutes.getSelectedIndex();
        List<Route> routes = routeService.getAllRoutes();
        if (index >= 0 && index < routes.size()) {
            Route r = routes.get(index);
            cmbStops.removeAllItems();
            for (String stop : r.getStops()) {
                cmbStops.addItem(stop);
            }

            // Populate table with buses on this route
            busTableModel.setRowCount(0);
            List<Bus> buses = busService.searchBus(r.getRouteId());
            for (Bus b : buses) {
                busTableModel.addRow(new Object[]{
                        b.getBusId(),
                        b.getBusNumber(),
                        b.getCapacity(),
                        b.getCurrentOccupancy(),
                        b.getAvailableSeats() + (b.isFull() ? " (FULL)" : " Free"),
                        b.getStatus(),
                        b.getCurrentLocation()
                });
            }
        }
    }

    // ==========================================================
    // Tab 3: Transport Attendance
    // ==========================================================
    private JPanel createAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topCard = UIUtils.createCardPanel();
        topCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        JButton btnMorning = UIUtils.createStyledButton("Mark Morning Boarding", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnMorning.addActionListener(e -> markSelfAttendance("Morning Pickup"));

        JButton btnEvening = UIUtils.createStyledButton("Mark Evening Boarding", UIUtils.COLOR_PRIMARY_DARK, Color.WHITE);
        btnEvening.addActionListener(e -> markSelfAttendance("Evening Drop"));

        topCard.add(new JLabel("Today: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + " |"));
        topCard.add(btnMorning);
        topCard.add(btnEvening);

        panel.add(topCard, BorderLayout.NORTH);

        String[] cols = {"Attendance ID", "Date", "Time", "Trip Type", "Bus", "Status"};
        attendanceTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblAttendance = new JTable(attendanceTableModel);
        UIUtils.formatTable(tblAttendance);

        panel.add(new JScrollPane(tblAttendance), BorderLayout.CENTER);
        refreshAttendanceHistory();
        return panel;
    }

    private void markSelfAttendance(String tripType) {
        if (student.getAssignedBusId() == null || student.getAssignedBusId().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enroll in an available bus first from the Routes tab.",
                    "No Bus Assigned", JOptionPane.WARNING_MESSAGE);
            return;
        }

        attendanceService.recordAttendance(student.getId(), student.getName(),
                student.getAssignedBusId(), 1, tripType, "Boarded");
        student.setBoardingStatus("On Board (" + tripType + ")");

        JOptionPane.showMessageDialog(this, "Attendance marked successfully as 'Boarded' for " + tripType + "!",
                "Attendance Recorded", JOptionPane.INFORMATION_MESSAGE);

        refreshAttendanceHistory();
        refreshLiveStatus();
    }

    private void refreshAttendanceHistory() {
        if (attendanceTableModel == null) return;
        attendanceTableModel.setRowCount(0);
        List<AttendanceRecord> list = attendanceService.getAttendanceForStudent(student.getId());
        DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter tFmt = DateTimeFormatter.ofPattern("hh:mm a");

        for (AttendanceRecord rec : list) {
            attendanceTableModel.addRow(new Object[]{
                    rec.getAttendanceId(),
                    rec.getDate().format(dFmt),
                    rec.getTime().format(tFmt),
                    rec.getTripType(),
                    rec.getBusId(),
                    rec.getStatus()
            });
        }
    }

    // ==========================================================
    // Tab 4: Submit Complaints
    // ==========================================================
    private JPanel createComplaintsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Submission Form
        JPanel form = UIUtils.createCardPanel();
        form.setLayout(new GridLayout(4, 2, 10, 10));

        JComboBox<String> cmbCategory = new JComboBox<>(new String[]{
                "Bus Delay / Unscheduled Wait",
                "Overcrowding / Standing Room",
                "Rash Driving / Overspeeding",
                "Driver Misbehavior",
                "Bus Cleanliness / Maintenance",
                "Route Detour / Missed Stop"
        });

        JTextField txtBus = new JTextField(student.getAssignedBusId() != null ? student.getAssignedBusId() : "KA-01-EXP-101");
        JTextArea txtDesc = new JTextArea(2, 20);

        JButton btnSubmit = UIUtils.createStyledButton("Submit Grievance", UIUtils.COLOR_DANGER, Color.WHITE);
        btnSubmit.addActionListener(e -> {
            String desc = txtDesc.getText().trim();
            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please describe the problem.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            complaintService.submitComplaint(student.getId(), student.getName(), txtBus.getText().trim(),
                    (String) cmbCategory.getSelectedItem(), desc);
            txtDesc.setText("");
            JOptionPane.showMessageDialog(this, "Complaint submitted to Transport Admin for review.",
                    "Complaint Lodged", JOptionPane.INFORMATION_MESSAGE);
            refreshComplaintsHistory();
        });

        form.add(new JLabel("Grievance Category:")); form.add(cmbCategory);
        form.add(new JLabel("Bus Number / ID:")); form.add(txtBus);
        form.add(new JLabel("Description of Issue:")); form.add(new JScrollPane(txtDesc));
        form.add(new JLabel("")); form.add(btnSubmit);

        panel.add(form, BorderLayout.NORTH);

        // Complaints Table
        String[] cols = {"Complaint ID", "Date", "Category", "Bus", "Status", "Admin Remarks"};
        complaintsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblComplaints = new JTable(complaintsTableModel);
        UIUtils.formatTable(tblComplaints);

        JScrollPane scroll = new JScrollPane(tblComplaints);
        scroll.setBorder(BorderFactory.createTitledBorder("My Submitted Complaints & Tracking"));
        panel.add(scroll, BorderLayout.CENTER);

        refreshComplaintsHistory();
        return panel;
    }

    private void refreshComplaintsHistory() {
        if (complaintsTableModel == null) return;
        complaintsTableModel.setRowCount(0);
        List<Complaint> list = complaintService.getComplaintsForStudent(student.getId());
        for (Complaint c : list) {
            complaintsTableModel.addRow(new Object[]{
                    c.getComplaintId(),
                    c.getSubmissionDate().toString(),
                    c.getCategory(),
                    c.getBusNumber(),
                    c.getStatus(),
                    c.getAdminRemarks()
            });
        }
    }

    // ==========================================================
    // Tab 5: Announcements Feed
    // ==========================================================
    private JPanel createAnnouncementsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        txtAnnouncements = new JTextArea();
        txtAnnouncements.setEditable(false);
        txtAnnouncements.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtAnnouncements.setBackground(Color.WHITE);

        JButton btnRefreshAnn = UIUtils.createStyledButton("Refresh Live Feed", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnRefreshAnn.addActionListener(e -> refreshAnnouncements());

        panel.add(btnRefreshAnn, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtAnnouncements), BorderLayout.CENTER);

        refreshAnnouncements();
        return panel;
    }

    private void refreshAnnouncements() {
        if (txtAnnouncements == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("COLLEGE TRANSPORT ANNOUNCEMENTS & LIVE BROADCAST FEED\n");
        sb.append("========================================================================\n\n");

        for (Announcement a : notificationService.getAllAnnouncements()) {
            if ("All".equalsIgnoreCase(a.getTargetAudience()) || "Students".equalsIgnoreCase(a.getTargetAudience())) {
                sb.append(a.toString()).append("\n\n");
            }
        }
        txtAnnouncements.setText(sb.toString());
    }

    private void refreshLiveStatus() {
        if (student.getAssignedBusId() != null && !student.getAssignedBusId().isEmpty()) {
            Bus b = busService.getBusById(student.getAssignedBusId());
            if (b != null) {
                lblAssignedBus.setText("Assigned Bus: " + b.getBusNumber() + " (" + b.getBusId() + ")");
                lblCurrentLocation.setText("Current Location: " + b.getCurrentLocation() + " | Pickup Stop: " +
                        (student.getPickupStop() != null ? student.getPickupStop() : "Not Selected"));
                lblBusStatus.setText("Operating Status: " + b.getStatus() + " | Seats Occupied: " +
                        b.getCurrentOccupancy() + " / " + b.getCapacity());

                Optional<Route> r = routeService.searchRoute(b.getRouteId());
                if (r.isPresent()) {
                    lblRouteDetails.setText("Route: " + r.get().getRouteName() + " (Stops: " + r.get().getStopsFormatted() + ")");
                }
            }
        } else {
            lblAssignedBus.setText("Assigned Bus: None (Please enroll in a bus under 'Available Routes' tab)");
            lblRouteDetails.setText("Route: None Selected");
            lblCurrentLocation.setText("Location: N/A");
            lblBusStatus.setText("Status: Unenrolled");
        }

        double rate = attendanceService.calculateAttendancePercentage(student.getId());
        lblAttendancePercent.setText(String.format("My Transport Attendance Rate: %.1f%%", rate));

        refreshAnnouncements();
    }

    private void logout() {
        new LoginFrame(new LoginService(), busService, routeService,
                attendanceService, complaintService, notificationService).setVisible(true);
        this.dispose();
    }
}
