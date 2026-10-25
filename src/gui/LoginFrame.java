package gui;

import exception.InvalidLoginException;
import model.Admin;
import model.Driver;
import model.Student;
import model.User;
import service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * CS23304 - Unit 5: AWT / Swing & Event Handling
 * LoginFrame provides a role-based login gateway for Students, Admins, and Drivers,
 * along with student registration and one-click demo credentials for evaluation.
 */
public class LoginFrame extends JFrame {
    private final LoginService loginService;
    private final BusService busService;
    private final RouteService routeService;
    private final AttendanceService attendanceService;
    private final ComplaintService complaintService;
    private final NotificationService notificationService;

    private JComboBox<String> roleCombo;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame(LoginService loginService, BusService busService, RouteService routeService,
                      AttendanceService attendanceService, ComplaintService complaintService,
                      NotificationService notificationService) {
        this.loginService = loginService;
        this.busService = busService;
        this.routeService = routeService;
        this.attendanceService = attendanceService;
        this.complaintService = complaintService;
        this.notificationService = notificationService;

        setTitle("Smart College Transport Management System - CS23304");
        setSize(560, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG);

        // Header Banner
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setBackground(UIUtils.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(22, 25, 22, 25));

        JLabel titleLabel = new JLabel("Smart College Transport System", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("CS23304 - Integrated 5-Unit Java Project Showcase", JLabel.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(224, 231, 255));

        headerPanel.add(titleLabel);
        headerPanel.add(subLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Card Panel
        JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        cardWrapper.setBackground(UIUtils.COLOR_BG);

        JPanel card = UIUtils.createCardPanel();
        card.setPreferredSize(new Dimension(480, 440));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel loginTitle = new JLabel("Sign In to Your Account");
        loginTitle.setFont(UIUtils.FONT_TITLE);
        loginTitle.setForeground(UIUtils.COLOR_TEXT_DARK);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Role Selector
        JLabel lblRole = new JLabel("Select User Role:");
        lblRole.setFont(UIUtils.FONT_BOLD);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] roles = {"Student", "Transport Admin", "Driver"};
        roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(UIUtils.FONT_BODY);
        roleCombo.setMaximumSize(new Dimension(440, 36));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Email Field
        JLabel lblEmail = new JLabel("Email Address / Institutional ID:");
        lblEmail.setFont(UIUtils.FONT_BOLD);
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField("aarav.student@college.edu");
        emailField.setFont(UIUtils.FONT_BODY);
        emailField.setMaximumSize(new Dimension(440, 36));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password Field
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UIUtils.FONT_BOLD);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField("student123");
        passwordField.setFont(UIUtils.FONT_BODY);
        passwordField.setMaximumSize(new Dimension(440, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Button (Unit 5: Event Handling)
        JButton btnLogin = UIUtils.createStyledButton("Authenticate & Log In", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnLogin.setMaximumSize(new Dimension(440, 42));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());

        // Register Button
        JButton btnRegister = UIUtils.createStyledButton("New Student? Register Here", new Color(100, 116, 139), Color.WHITE);
        btnRegister.setMaximumSize(new Dimension(440, 36));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.addActionListener(e -> openRegistrationDialog());

        // Quick Fill Demo Buttons Panel
        JLabel lblDemo = new JLabel("Quick Fill Demo Accounts (For Viva / Evaluation):");
        lblDemo.setFont(UIUtils.FONT_SMALL);
        lblDemo.setForeground(UIUtils.COLOR_TEXT_MUTED);
        lblDemo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel demoButtons = new JPanel(new GridLayout(1, 3, 8, 0));
        demoButtons.setOpaque(false);
        demoButtons.setMaximumSize(new Dimension(440, 32));

        JButton btnDemoStu = new JButton("Student");
        btnDemoStu.setFont(UIUtils.FONT_SMALL);
        btnDemoStu.addActionListener(e -> {
            roleCombo.setSelectedItem("Student");
            emailField.setText("aarav.student@college.edu");
            passwordField.setText("student123");
        });

        JButton btnDemoAdmin = new JButton("Admin");
        btnDemoAdmin.setFont(UIUtils.FONT_SMALL);
        btnDemoAdmin.addActionListener(e -> {
            roleCombo.setSelectedItem("Transport Admin");
            emailField.setText("admin@college.edu");
            passwordField.setText("admin123");
        });

        JButton btnDemoDriver = new JButton("Driver");
        btnDemoDriver.setFont(UIUtils.FONT_SMALL);
        btnDemoDriver.addActionListener(e -> {
            roleCombo.setSelectedItem("Driver");
            emailField.setText("ramesh.driver@college.edu");
            passwordField.setText("driver123");
        });

        demoButtons.add(btnDemoStu);
        demoButtons.add(btnDemoAdmin);
        demoButtons.add(btnDemoDriver);

        // Layout Assembly
        card.add(Box.createVerticalStrut(5));
        card.add(loginTitle);
        card.add(Box.createVerticalStrut(15));
        card.add(lblRole);
        card.add(Box.createVerticalStrut(4));
        card.add(roleCombo);
        card.add(Box.createVerticalStrut(10));
        card.add(lblEmail);
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));
        card.add(lblPass);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(16));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(14));
        card.add(lblDemo);
        card.add(Box.createVerticalStrut(6));
        card.add(demoButtons);

        cardWrapper.add(card);
        mainPanel.add(cardWrapper, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    /**
     * Handles authentication click. Catches InvalidLoginException.
     */
    private void performLogin() {
        String rawRole = (String) roleCombo.getSelectedItem();
        String expectedRole = "Transport Admin".equalsIgnoreCase(rawRole) ? "Admin" : rawRole;
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            // Unit 3: Exception Handling
            User user = loginService.authenticate(email, password, expectedRole);
            DataPersistenceService.logEvent("User logged in successfully: " + user.getName() + " (" + user.getRole() + ")");

            // Navigate to appropriate role dashboard
            if (user instanceof Student) {
                new StudentDashboard((Student) user, busService, routeService,
                        attendanceService, complaintService, notificationService).setVisible(true);
            } else if (user instanceof Admin) {
                new AdminDashboard((Admin) user, busService, routeService,
                        attendanceService, complaintService, notificationService, loginService).setVisible(true);
            } else if (user instanceof Driver) {
                new DriverDashboard((Driver) user, busService, routeService,
                        attendanceService, notificationService, loginService).setVisible(true);
            }
            this.dispose();

        } catch (InvalidLoginException ex) {
            // Unit 3: Catch user-defined exception and alert user
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Authentication Failed (InvalidLoginException)",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Student Self-Registration", true);
        dialog.setSize(440, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtPhone = new JTextField();
        JTextField txtRoll = new JTextField();
        JComboBox<String> cmbDept = new JComboBox<>(new String[]{"Computer Science", "Information Technology", "Electronics & Comm.", "Mechanical", "Civil"});
        JComboBox<Integer> cmbYear = new JComboBox<>(new Integer[]{1, 2, 3, 4});

        form.add(new JLabel("Full Name:")); form.add(txtName);
        form.add(new JLabel("Email Address:")); form.add(txtEmail);
        form.add(new JLabel("Set Password:")); form.add(txtPass);
        form.add(new JLabel("Contact Number:")); form.add(txtPhone);
        form.add(new JLabel("Roll Number:")); form.add(txtRoll);
        form.add(new JLabel("Department:")); form.add(cmbDept);
        form.add(new JLabel("Current Year:")); form.add(cmbYear);

        JPanel btnPanel = new JPanel();
        JButton btnSubmit = UIUtils.createStyledButton("Submit Registration", UIUtils.COLOR_SUCCESS, Color.WHITE);
        btnSubmit.addActionListener(e -> {
            try {
                loginService.registerStudent(txtName.getText().trim(), txtEmail.getText().trim(),
                        new String(txtPass.getPassword()), txtPhone.getText().trim(),
                        txtRoll.getText().trim(), (String) cmbDept.getSelectedItem(), (Integer) cmbYear.getSelectedItem());

                JOptionPane.showMessageDialog(dialog, "Registration successful! You may now log in.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                emailField.setText(txtEmail.getText().trim());
                passwordField.setText(new String(txtPass.getPassword()));
                roleCombo.setSelectedItem("Student");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(btnSubmit);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
