import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class Report extends BaseFrame {
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> reportTypeComboBox;
    private JComboBox<String> courseComboBox;

    public Report(String username) {
        super("Reports", username);
        initializeUI();
    }

    private void initializeUI() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Attendance Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);

        // Filter panel
        JPanel filterPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        filterPanel.add(new JLabel("Report Type:"));
        reportTypeComboBox = new JComboBox<>(new String[]{
            "Daily Report", "Weekly Report", "Monthly Report", "Course Summary"
        });

        filterPanel.add(new JLabel("Course:"));
        courseComboBox = new JComboBox<>();
        courseComboBox.addItem("All Courses");
        loadCourses();

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        // Table
        String[] columnNames = {"Student ID", "Name", "Course", "Present", "Absent", "Percentage"};
        tableModel = new DefaultTableModel(columnNames, 0);
        reportTable = new JTable(tableModel);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        reportTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JButton generateBtn = createActionButton(" Generate Report", PRIMARY_COLOR);
        JButton viewBtn = createActionButton(" View Records", new Color(0, 123, 255)); // Blue
        JButton printBtn = createActionButton(" Print", SECONDARY_COLOR);

        generateBtn.addActionListener(e -> generateReport());
        viewBtn.addActionListener(e -> viewRecords());
        printBtn.addActionListener(e -> printReport());

        buttonPanel.add(generateBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(printBtn);

        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 35));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e) { button.setBackground(color); }
        });

        return button;
    }

    private void loadCourses() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT course FROM students ORDER BY course")) {
            
            while (rs.next()) {
                courseComboBox.addItem(rs.getString("course"));
            }
        } catch (SQLException ex) {
            showError("Error loading courses: " + ex.getMessage());
        }
    }

    private void generateReport() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        String course = (String) courseComboBox.getSelectedItem();
        
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "";
            
            switch (reportType) {
                case "Course Summary":
                    query = "SELECT s.student_id, s.name, s.course, " +
                            "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) as present, " +
                            "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) as absent, " +
                            "ROUND(SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 1) as percentage " +
                            "FROM students s LEFT JOIN attendance a ON s.student_id = a.student_id " +
                            "WHERE ? = 'All Courses' OR s.course = ? " +
                            "GROUP BY s.student_id, s.name, s.course " +
                            "ORDER BY s.course, s.name";
                    break;
                    
                case "Daily Report":
                    query = "SELECT s.student_id, s.name, s.course, " +
                            "SUM(CASE WHEN a.status = 'Present' AND a.date = CURDATE() THEN 1 ELSE 0 END) as present, " +
                            "SUM(CASE WHEN a.status = 'Absent' AND a.date = CURDATE() THEN 1 ELSE 0 END) as absent, " +
                            "ROUND(SUM(CASE WHEN a.status = 'Present' AND a.date = CURDATE() THEN 1 ELSE 0 END) * 100.0 / " +
                            "GREATEST(COUNT(CASE WHEN a.date = CURDATE() THEN 1 END), 1), 1) as percentage " +
                            "FROM students s LEFT JOIN attendance a ON s.student_id = a.student_id " +
                            "WHERE (? = 'All Courses' OR s.course = ?) " +
                            "GROUP BY s.student_id, s.name, s.course " +
                            "ORDER BY s.course, s.name";
                    break;
                    
                case "Weekly Report":
                    query = "SELECT s.student_id, s.name, s.course, " +
                            "SUM(CASE WHEN a.status = 'Present' AND YEARWEEK(a.date) = YEARWEEK(CURDATE()) THEN 1 ELSE 0 END) as present, " +
                            "SUM(CASE WHEN a.status = 'Absent' AND YEARWEEK(a.date) = YEARWEEK(CURDATE()) THEN 1 ELSE 0 END) as absent, " +
                            "ROUND(SUM(CASE WHEN a.status = 'Present' AND YEARWEEK(a.date) = YEARWEEK(CURDATE()) THEN 1 ELSE 0 END) * 100.0 / " +
                            "GREATEST(COUNT(CASE WHEN YEARWEEK(a.date) = YEARWEEK(CURDATE()) THEN 1 END), 1), 1) as percentage " +
                            "FROM students s LEFT JOIN attendance a ON s.student_id = a.student_id " +
                            "WHERE (? = 'All Courses' OR s.course = ?) " +
                            "GROUP BY s.student_id, s.name, s.course " +
                            "ORDER BY s.course, s.name";
                    break;
                    
                case "Monthly Report":
                    query = "SELECT s.student_id, s.name, s.course, " +
                            "SUM(CASE WHEN a.status = 'Present' AND MONTH(a.date) = MONTH(CURDATE()) AND YEAR(a.date) = YEAR(CURDATE()) THEN 1 ELSE 0 END) as present, " +
                            "SUM(CASE WHEN a.status = 'Absent' AND MONTH(a.date) = MONTH(CURDATE()) AND YEAR(a.date) = YEAR(CURDATE()) THEN 1 ELSE 0 END) as absent, " +
                            "ROUND(SUM(CASE WHEN a.status = 'Present' AND MONTH(a.date) = MONTH(CURDATE()) AND YEAR(a.date) = YEAR(CURDATE()) THEN 1 ELSE 0 END) * 100.0 / " +
                            "GREATEST(COUNT(CASE WHEN MONTH(a.date) = MONTH(CURDATE()) AND YEAR(a.date) = YEAR(CURDATE()) THEN 1 END), 1), 1) as percentage " +
                            "FROM students s LEFT JOIN attendance a ON s.student_id = a.student_id " +
                            "WHERE (? = 'All Courses' OR s.course = ?) " +
                            "GROUP BY s.student_id, s.name, s.course " +
                            "ORDER BY s.course, s.name";
                    break;
            }
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, course);
                ps.setString(2, course);
                
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getInt("present"),
                        rs.getInt("absent"),
                        rs.getDouble("percentage") + "%"
                    });
                }
            }
            
            JOptionPane.showMessageDialog(this, reportType + " generated successfully!");
            
        } catch (SQLException ex) {
            showError("Error generating report: " + ex.getMessage());
        }
    }

    private void viewRecords() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"Student ID", "Name", "Course", "Date", "Status"});

        String course = (String) courseComboBox.getSelectedItem();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT s.student_id, s.name, s.course, a.date, a.status " +
                 "FROM students s JOIN attendance a ON s.student_id = a.student_id " +
                 "WHERE (? = 'All Courses' OR s.course = ?) " +
                 "ORDER BY a.date DESC, s.name")) {

            ps.setString(1, course);
            ps.setString(2, course);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("course"),
                    rs.getDate("date"),
                    rs.getString("status")
                });
            }

            JOptionPane.showMessageDialog(this, "Records loaded successfully!");

        } catch (SQLException ex) {
            showError("Error loading records: " + ex.getMessage());
        }
    }

    private void printReport() {
        try {
            reportTable.print();
        } catch (java.awt.print.PrinterException ex) {
            showError("Error printing report: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
