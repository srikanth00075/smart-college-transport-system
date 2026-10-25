package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * CS23304 - Unit 5: AWT / Swing GUI Styling Utility
 * Provides a clean, modern aesthetic with consistent color palettes,
 * custom typography, and styled Swing components.
 */
public class UIUtils {
    // Modern Color Palette
    public static final Color COLOR_PRIMARY = new Color(30, 58, 138);     // Deep Indigo
    public static final Color COLOR_PRIMARY_DARK = new Color(23, 37, 84); // Midnight Blue
    public static final Color COLOR_ACCENT = new Color(14, 165, 233);     // Sky Blue Accent
    public static final Color COLOR_SUCCESS = new Color(16, 185, 129);    // Emerald Green
    public static final Color COLOR_WARNING = new Color(245, 158, 11);    // Amber Orange
    public static final Color COLOR_DANGER = new Color(239, 68, 68);      // Crimson Red
    public static final Color COLOR_BG = new Color(248, 250, 252);        // Slate Light Gray
    public static final Color COLOR_CARD = Color.WHITE;
    public static final Color COLOR_TEXT_DARK = new Color(15, 23, 42);    // Dark Slate
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);// Muted Gray

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Creates a styled modern push button.
     */
    public static JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    /**
     * Creates a standardized card panel with subtle border.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        return panel;
    }

    /**
     * Styles JTable headers and row stripes.
     */
    public static void formatTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(COLOR_TEXT_DARK);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SUBHEADER);
        header.setBackground(COLOR_PRIMARY_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    /**
     * Creates a header banner panel.
     */
    public static JPanel createHeaderBanner(String title, String subtitle, String userRoleName) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(16, 22, 16, 22));

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);

        JLabel lblTitle = new JLabel("[BUS] " + title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_BODY);
        lblSub.setForeground(new Color(224, 231, 255));

        titles.add(lblTitle);
        titles.add(lblSub);

        JLabel lblRole = new JLabel("Role: " + userRoleName + "  ");
        lblRole.setFont(FONT_BOLD);
        lblRole.setForeground(Color.WHITE);

        headerPanel.add(titles, BorderLayout.WEST);
        headerPanel.add(lblRole, BorderLayout.EAST);

        return headerPanel;
    }
}
