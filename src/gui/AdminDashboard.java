package gui;

import model.*;
import service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CS23304 - Unit 5: AWT / Swing GUI & Event Handling
 * AdminDashboard empowers the Transport Administrator to:
 * - Manage buses, routes, and drivers
 * - Assign buses to routes and link drivers
 * - Review and update student complaints
 * - Broadcast college-wide transport announcements
 * - Perform File I/O exports and binary Object Serialization backups
 */
public class AdminDashboard extends JFrame {
    private final Admin admin;
    private final BusService busService;
    private final RouteService routeService;
    private final AttendanceService attendanceService;
    private final ComplaintService complaintService;
    private final NotificationService notificationService;
    private final LoginService loginService;

    // Table models
    private DefaultTableModel busTableModel;
    private DefaultTableModel routeTableModel;
    private DefaultTableModel complaintTableModel;
    private DefaultTableModel attendanceTableModel;
    private DefaultTableModel driverTableModel;

    private JTable tblComplaints;

    public AdminDashboard(Admin admin, BusService busService, RouteService routeService,
                          AttendanceService attendanceService, ComplaintService complaintService,
                          NotificationService notificationService, LoginService loginService) {
        this.admin = admin;
        this.busService = busService;
        this.routeService = routeService;
        this.attendanceService = attendanceService;
        this.complaintService = complaintService;
        this.notificationService = notificationService;
        this.loginService = loginService;

        setTitle("College Transport Directorate - Administrator Control Panel");
        setSize(980, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.COLOR_BG);

        // Top Banner
        JPanel header = UIUtils.createHeaderBanner(
                "College Transport Directorate - Admin Control Panel",
                "Administrator: " + admin.getName() + " | Office: " + admin.getOfficeRoom(),
                "Transport Admin"
        );

        JButton btnLogout = UIUtils.createStyledButton("Logout", UIUtils.COLOR_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> logout());
        header.add(btnLogout, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.FONT_SUBHEADER);

        tabs.addTab("Fleet Management", createBusManagementTab());
        tabs.addTab("Route Management", createRouteManagementTab());
        tabs.addTab("Driver & Bus Allocation", createDriverAllocationTab());
        tabs.addTab("Complaints Desk", createComplaintsDeskTab());
        tabs.addTab("Attendance Logs", createAttendanceOverviewTab());
        tabs.addTab("Broadcast Alerts", createBroadcastTab());
        tabs.addTab("File I/O & Backup", createBackupAndReportsTab());

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ==========================================================
    // Tab 1: Bus Fleet Management
    // ==========================================================
    private JPanel createBusManagementTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Add Bus Form
        JPanel form = UIUtils.createCardPanel();
        form.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

        JTextField txtBusId = new JTextField("BUS-105", 7);
        JTextField txtBusNum = new JTextField("KA-01-EXP-105", 10);
        JTextField txtCap = new JTextField("50", 4);
        JTextField txtRoute = new JTextField("1", 4);

        JButton btnAddBus = UIUtils.createStyledButton("Add Bus", UIUtils.COLOR_SUCCESS, Color.WHITE);
        btnAddBus.addActionListener(e -> {
            try {
                int cap = Integer.parseInt(txtCap.getText().trim());
                int rId = Integer.parseInt(txtRoute.getText().trim());
                Bus newBus = new Bus(txtBusId.getText().trim(), txtBusNum.getText().trim(), cap, rId, null);
                busService.addBus(newBus);
                refreshBusTable();
                JOptionPane.showMessageDialog(this, "Bus " + newBus.getBusNumber() + " added to fleet.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnDeleteBus = UIUtils.createStyledButton("Remove Selected Bus", UIUtils.COLOR_DANGER, Color.WHITE);
        btnDeleteBus.addActionListener(e -> {
            int row = ((JTable) ((JScrollPane) panel.getComponent(1)).getViewport().getView()).getSelectedRow();
            if (row >= 0) {
                String busId = (String) busTableModel.getValueAt(row, 0);
                busService.removeBus(busId);
                refreshBusTable();
                JOptionPane.showMessageDialog(this, "Bus " + busId + " removed.");
            } else {
                JOptionPane.showMessageDialog(this, "Select a bus to remove.");
            }
        });

        form.add(new JLabel("Bus ID:")); form.add(txtBusId);
        form.add(new JLabel("Bus Plate:")); form.add(txtBusNum);
        form.add(new JLabel("Capacity:")); form.add(txtCap);
        form.add(new JLabel("Route ID:")); form.add(txtRoute);
        form.add(btnAddBus);
        form.add(btnDeleteBus);

        panel.add(form, BorderLayout.NORTH);

        // Table
        String[] cols = {"Bus ID", "Bus Number", "Capacity", "Occupied", "Available", "Route ID", "Driver ID", "Status", "Location"};
        busTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(busTableModel);
        UIUtils.formatTable(tbl);

        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        refreshBusTable();
        return panel;
    }

    private void refreshBusTable() {
        if (busTableModel == null) return;
        busTableModel.setRowCount(0);
        for (Bus b : busService.getAllBuses()) {
            busTableModel.addRow(new Object[]{
                    b.getBusId(),
                    b.getBusNumber(),
                    b.getCapacity(),
                    b.getCurrentOccupancy(),
                    b.getAvailableSeats(),
                    b.getRouteId(),
                    (b.getDriverId() != null ? b.getDriverId() : "Unassigned"),
                    b.getStatus(),
                    b.getCurrentLocation()
            });
        }
    }

    // ==========================================================
    // Tab 2: Route Management
    // ==========================================================
    private JPanel createRouteManagementTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Form
        JPanel form = UIUtils.createCardPanel();
        form.setLayout(new GridLayout(3, 4, 8, 8));

        JTextField txtRId = new JTextField("4");
        JTextField txtRName = new JTextField("Route 4 - Western Corridor");
        JTextField txtStart = new JTextField("West City Terminal");
        JTextField txtEnd = new JTextField("College Campus");
        JTextField txtStops = new JTextField("West Terminal, Silicon City, College Campus");

        JButton btnAddRoute = UIUtils.createStyledButton("Create Route", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnAddRoute.addActionListener(e -> {
            try {
                int rId = Integer.parseInt(txtRId.getText().trim());
                Route r = new Route(rId, txtRName.getText().trim(), txtStart.getText().trim(),
                        txtEnd.getText().trim(), LocalTime.of(7, 20), LocalTime.of(16, 50));
                for (String s : txtStops.getText().split(",")) {
                    r.addStop(s.trim());
                }
                routeService.addRoute(r);
                refreshRouteTable();
                JOptionPane.showMessageDialog(this, "Route created successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(new JLabel("Route ID:")); form.add(txtRId);
        form.add(new JLabel("Route Name:")); form.add(txtRName);
        form.add(new JLabel("Start Point:")); form.add(txtStart);
        form.add(new JLabel("Destination:")); form.add(txtEnd);
        form.add(new JLabel("Stops (comma-sep):")); form.add(txtStops);
        form.add(new JLabel("")); form.add(btnAddRoute);

        panel.add(form, BorderLayout.NORTH);

        String[] cols = {"Route ID", "Route Name", "Start Point", "Destination", "Stops"};
        routeTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(routeTableModel);
        UIUtils.formatTable(tbl);

        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        refreshRouteTable();
        return panel;
    }

    private void refreshRouteTable() {
        if (routeTableModel == null) return;
        routeTableModel.setRowCount(0);
        for (Route r : routeService.getAllRoutes()) {
            routeTableModel.addRow(new Object[]{
                    r.getRouteId(),
                    r.getRouteName(),
                    r.getStartingPoint(),
                    r.getDestination(),
                    r.getStopsFormatted()
            });
        }
    }

    // ==========================================================
    // Tab 3: Driver & Bus Allocation
    // ==========================================================
    private JPanel createDriverAllocationTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Assignment Form
        JPanel form = UIUtils.createCardPanel();
        form.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        JComboBox<String> cmbDriver = new JComboBox<>();
        for (Driver d : loginService.getAllDrivers()) {
            cmbDriver.addItem(d.getId() + " - " + d.getName());
        }

        JComboBox<String> cmbBus = new JComboBox<>();
        for (Bus b : busService.getAllBuses()) {
            cmbBus.addItem(b.getBusId() + " (" + b.getBusNumber() + ")");
        }

        JButton btnAssign = UIUtils.createStyledButton("Assign Driver to Bus", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnAssign.addActionListener(e -> {
            String selD = (String) cmbDriver.getSelectedItem();
            String selB = (String) cmbBus.getSelectedItem();
            if (selD != null && selB != null) {
                String dId = selD.split(" - ")[0];
                String bId = selB.split(" ")[0];
                Bus b = busService.getBusById(bId);
                if (b != null) {
                    b.setDriverId(dId);
                    refreshDriverTable();
                    refreshBusTable();
                    JOptionPane.showMessageDialog(this, "Driver " + dId + " assigned to " + bId);
                }
            }
        });

        form.add(new JLabel("Select Driver:")); form.add(cmbDriver);
        form.add(new JLabel("Select Bus:")); form.add(cmbBus);
        form.add(btnAssign);

        panel.add(form, BorderLayout.NORTH);

        String[] cols = {"Driver ID", "Name", "License", "Experience", "Contact", "Assigned Bus"};
        driverTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(driverTableModel);
        UIUtils.formatTable(tbl);

        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        refreshDriverTable();
        return panel;
    }

    private void refreshDriverTable() {
        if (driverTableModel == null) return;
        driverTableModel.setRowCount(0);
        for (Driver d : loginService.getAllDrivers()) {
            driverTableModel.addRow(new Object[]{
                    d.getId(),
                    d.getName(),
                    d.getLicenseNumber(),
                    d.getExperienceYears() + " Years",
                    d.getContactNumber(),
                    (d.getAssignedBusId() != null ? d.getAssignedBusId() : "None")
            });
        }
    }

    // ==========================================================
    // Tab 4: Complaints Desk (Review & Resolve)
    // ==========================================================
    private JPanel createComplaintsDeskTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Action card
        JPanel action = UIUtils.createCardPanel();
        action.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));

        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"In Review", "Resolved", "Pending"});
        JTextField txtRemarks = new JTextField(25);
        JButton btnUpdateStatus = UIUtils.createStyledButton("Update Selected Complaint", UIUtils.COLOR_PRIMARY, Color.WHITE);

        btnUpdateStatus.addActionListener(e -> {
            int row = tblComplaints.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a complaint row from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String cId = (String) complaintTableModel.getValueAt(row, 0);
            String st = (String) cmbStatus.getSelectedItem();
            String rem = txtRemarks.getText().trim();
            complaintService.updateComplaintStatus(cId, st, rem.isEmpty() ? "Resolved by transport administration." : rem);
            refreshComplaintsDesk();
            JOptionPane.showMessageDialog(this, "Complaint " + cId + " updated to '" + st + "'.");
        });

        action.add(new JLabel("Set Status:")); action.add(cmbStatus);
        action.add(new JLabel("Admin Remarks:")); action.add(txtRemarks);
        action.add(btnUpdateStatus);

        panel.add(action, BorderLayout.NORTH);

        String[] cols = {"Complaint ID", "Date", "Student Name", "Bus", "Category", "Description", "Status", "Admin Remarks"};
        complaintTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblComplaints = new JTable(complaintTableModel);
        UIUtils.formatTable(tblComplaints);

        panel.add(new JScrollPane(tblComplaints), BorderLayout.CENTER);
        refreshComplaintsDesk();
        return panel;
    }

    private void refreshComplaintsDesk() {
        if (complaintTableModel == null) return;
        complaintTableModel.setRowCount(0);
        for (Complaint c : complaintService.getAllComplaints()) {
            complaintTableModel.addRow(new Object[]{
                    c.getComplaintId(),
                    c.getSubmissionDate().toString(),
                    c.getStudentName(),
                    c.getBusNumber(),
                    c.getCategory(),
                    c.getDescription(),
                    c.getStatus(),
                    c.getAdminRemarks()
            });
        }
    }

    // ==========================================================
    // Tab 5: Attendance Overview
    // ==========================================================
    private JPanel createAttendanceOverviewTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"ID", "Date", "Time", "Student Name", "Roll / ID", "Bus ID", "Trip Type", "Status"};
        attendanceTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(attendanceTableModel);
        UIUtils.formatTable(tbl);

        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        refreshAttendanceLog();
        return panel;
    }

    private void refreshAttendanceLog() {
        if (attendanceTableModel == null) return;
        attendanceTableModel.setRowCount(0);
        for (AttendanceRecord r : attendanceService.getAllAttendanceRecords()) {
            attendanceTableModel.addRow(new Object[]{
                    r.getAttendanceId(),
                    r.getDate().toString(),
                    r.getTime().toString(),
                    r.getStudentName(),
                    r.getStudentId(),
                    r.getBusId(),
                    r.getTripType(),
                    r.getStatus()
            });
        }
    }

    // ==========================================================
    // Tab 6: Broadcast Announcements
    // ==========================================================
    private JPanel createBroadcastTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new GridLayout(5, 2, 12, 12));

        JTextField txtTitle = new JTextField();
        JTextArea txtMsg = new JTextArea();
        JComboBox<String> cmbAudience = new JComboBox<>(new String[]{"All", "Students", "Drivers"});

        JButton btnBroadcast = UIUtils.createStyledButton("Broadcast Live Notice", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnBroadcast.addActionListener(e -> {
            String title = txtTitle.getText().trim();
            String msg = txtMsg.getText().trim();
            if (title.isEmpty() || msg.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and Message are required.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Announcement a = new Announcement(title, msg, admin.getName(), (String) cmbAudience.getSelectedItem());
            notificationService.broadcastAnnouncement(a);
            JOptionPane.showMessageDialog(this, "Announcement broadcasted to " + cmbAudience.getSelectedItem() + " dashboards!",
                    "Broadcast Sent", JOptionPane.INFORMATION_MESSAGE);
            txtTitle.setText("");
            txtMsg.setText("");
        });

        card.add(new JLabel("Announcement Title:")); card.add(txtTitle);
        card.add(new JLabel("Target Audience:")); card.add(cmbAudience);
        card.add(new JLabel("Message Content:")); card.add(new JScrollPane(txtMsg));
        card.add(new JLabel("")); card.add(btnBroadcast);

        panel.add(card, BorderLayout.NORTH);
        return panel;
    }

    // ==========================================================
    // Tab 7: Unit 4 File Handling & Serialization Demonstrator
    // ==========================================================
    private JPanel createBackupAndReportsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(UIUtils.COLOR_BG);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Card 1: Attendance CSV Export
        JPanel card1 = UIUtils.createCardPanel();
        card1.setLayout(new BorderLayout(10, 10));
        JLabel lblC1 = new JLabel("Export Attendance Log (CSV)");
        lblC1.setFont(UIUtils.FONT_HEADER);
        JTextArea desc1 = new JTextArea("Unit 4: File Handling demo using FileWriter and BufferedWriter.\nExports full transport attendance register to CSV format.");
        desc1.setEditable(false);
        JButton btnExportAtt = UIUtils.createStyledButton("Export Attendance CSV", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnExportAtt.addActionListener(e -> {
            File dest = new File("attendance_report.csv");
            boolean ok = DataPersistenceService.exportAttendanceToCSV(dest, attendanceService.getAllAttendanceRecords());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Attendance successfully saved to: " + dest.getAbsolutePath(),
                        "File Export Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        card1.add(lblC1, BorderLayout.NORTH);
        card1.add(desc1, BorderLayout.CENTER);
        card1.add(btnExportAtt, BorderLayout.SOUTH);

        // Card 2: Complaints Text Report Export
        JPanel card2 = UIUtils.createCardPanel();
        card2.setLayout(new BorderLayout(10, 10));
        JLabel lblC2 = new JLabel("Export Complaints Report (TXT)");
        lblC2.setFont(UIUtils.FONT_HEADER);
        JTextArea desc2 = new JTextArea("Unit 4: File Handling demo using PrintWriter.\nExports formatted grievance report for transport audit.");
        desc2.setEditable(false);
        JButton btnExportCmp = UIUtils.createStyledButton("Export Complaints TXT", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnExportCmp.addActionListener(e -> {
            File dest = new File("complaints_audit_report.txt");
            boolean ok = DataPersistenceService.exportComplaintsReport(dest, complaintService.getAllComplaints());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Complaints report saved to: " + dest.getAbsolutePath(),
                        "File Export Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        card2.add(lblC2, BorderLayout.NORTH);
        card2.add(desc2, BorderLayout.CENTER);
        card2.add(btnExportCmp, BorderLayout.SOUTH);

        // Card 3: Object Serialization Snapshot
        JPanel card3 = UIUtils.createCardPanel();
        card3.setLayout(new BorderLayout(10, 10));
        JLabel lblC3 = new JLabel("System Snapshot (Serialization)");
        lblC3.setFont(UIUtils.FONT_HEADER);
        JTextArea desc3 = new JTextArea("Unit 4: Object Serialization demo using ObjectOutputStream.\nSerializes full fleet and routes to 'system_snapshot.ser'.");
        desc3.setEditable(false);
        JButton btnSerialize = UIUtils.createStyledButton("Serialize State to Disk", UIUtils.COLOR_SUCCESS, Color.WHITE);
        btnSerialize.addActionListener(e -> {
            File dest = new File("system_snapshot.ser");
            ArrayList<Bus> buses = new ArrayList<>(busService.getAllBuses());
            boolean ok = DataPersistenceService.saveObjectSnapshot(dest, buses);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Serialized " + buses.size() + " bus objects to: " + dest.getName(),
                        "Serialization Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        card3.add(lblC3, BorderLayout.NORTH);
        card3.add(desc3, BorderLayout.CENTER);
        card3.add(btnSerialize, BorderLayout.SOUTH);

        // Card 4: Deserialization
        JPanel card4 = UIUtils.createCardPanel();
        card4.setLayout(new BorderLayout(10, 10));
        JLabel lblC4 = new JLabel("Restore State (Deserialization)");
        lblC4.setFont(UIUtils.FONT_HEADER);
        JTextArea desc4 = new JTextArea("Unit 4: Object Deserialization demo using ObjectInputStream.\nReads back binary objects from disk snapshot.");
        desc4.setEditable(false);
        JButton btnDeserialize = UIUtils.createStyledButton("Deserialize State", UIUtils.COLOR_WARNING, Color.WHITE);
        btnDeserialize.addActionListener(e -> {
            File src = new File("system_snapshot.ser");
            if (!src.exists()) {
                JOptionPane.showMessageDialog(this, "Snapshot file 'system_snapshot.ser' does not exist yet. Please create a snapshot first.",
                        "File Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object obj = DataPersistenceService.loadObjectSnapshot(src);
            if (obj instanceof ArrayList) {
                JOptionPane.showMessageDialog(this, "Deserialization successful! Restored object instance from disk.",
                        "Deserialization Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        card4.add(lblC4, BorderLayout.NORTH);
        card4.add(desc4, BorderLayout.CENTER);
        card4.add(btnDeserialize, BorderLayout.SOUTH);

        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        panel.add(card4);

        return panel;
    }

    private void logout() {
        new LoginFrame(loginService, busService, routeService,
                attendanceService, complaintService, notificationService).setVisible(true);
        this.dispose();
    }
}
