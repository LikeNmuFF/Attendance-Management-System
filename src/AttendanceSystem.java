import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.*;

public class AttendanceSystem extends BaseFrame {
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> courseComboBox;
    private JTable studentTable;
    private DefaultTableModel studentModel;
    private JPanel studentPanel;
    private JComboBox<String> dateComboBox;

    public AttendanceSystem(String username) {
        super("Attendance System", username);
        initializeUI();
    }

    private void initializeUI() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(" Attendance Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Filter by Course:"));
        courseComboBox = new JComboBox<>();
        courseComboBox.addItem("All Courses");
        loadCourses();

        JButton filterBtn = new JButton(" Filter");
        styleButton(filterBtn, SECONDARY_COLOR);
        filterBtn.addActionListener(e -> filterAttendance());

        filterPanel.add(courseComboBox);
        filterPanel.add(filterBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        // Attendance records table
        String[] columnNames = {"Student ID", "Name", "Date", "Status", "Course"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        attendanceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        attendanceTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Student panel (for marking attendance)
        studentPanel = createMarkAttendancePanel();

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JButton exportBtn = createActionButton(" Export Data", new Color(46, 204, 113));
        exportBtn.addActionListener(e -> exportData());

        buttonPanel.add(exportBtn);

        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(studentPanel, BorderLayout.EAST);
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadAttendance();
    }

    private JPanel createMarkAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Mark Attendance"));
        panel.setPreferredSize(new Dimension(500, 400));

        // Date selection panel
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        datePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        datePanel.add(new JLabel("Date:"));
        
        dateComboBox = new JComboBox<>();
        dateComboBox.addItem(LocalDate.now().toString()); // Today's date
        dateComboBox.setEditable(true);
        dateComboBox.setPreferredSize(new Dimension(120, 25));
        datePanel.add(dateComboBox);
        
        panel.add(datePanel, BorderLayout.NORTH);

        // Student table
        String[] studentCols = {"Select", "Student ID", "Name", "Status"};
        studentModel = new DefaultTableModel(studentCols, 0) {
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
        };
        
        studentTable = new JTable(studentModel);
        studentTable.setRowHeight(25);
        
        // Set up status column with combo box
        TableColumn statusColumn = studentTable.getColumnModel().getColumn(3);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Present", "Absent", "Late", "Excused"});
        statusColumn.setCellEditor(new DefaultCellEditor(statusCombo));

        JScrollPane studentScroll = new JScrollPane(studentTable);
        panel.add(studentScroll, BorderLayout.CENTER);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton loadBtn = createActionButton("Load Students", PRIMARY_COLOR);
        JButton saveBtn = createActionButton("Save Attendance", new Color(46, 134, 193));
        JButton selectAllBtn = createActionButton("Select All", new Color(52, 152, 219));
        JButton clearAllBtn = createActionButton("Clear All", new Color(231, 76, 60));

        loadBtn.addActionListener(e -> loadStudents());
        saveBtn.addActionListener(e -> saveAttendance());
        selectAllBtn.addActionListener(e -> selectAllStudents(true));
        clearAllBtn.addActionListener(e -> selectAllStudents(false));

        actionPanel.add(loadBtn);
        actionPanel.add(saveBtn);
        actionPanel.add(selectAllBtn);
        actionPanel.add(clearAllBtn);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void selectAllStudents(boolean select) {
        for (int i = 0; i < studentModel.getRowCount(); i++) {
            studentModel.setValueAt(select, i, 0);
        }
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e) { button.setBackground(color); }
        });

        return button;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 30));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e) { button.setBackground(color); }
        });
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

    private void loadAttendance() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT a.student_id, s.name, a.date, a.status, s.course " +
                 "FROM attendance a JOIN students s ON a.student_id = s.id ORDER BY a.date DESC")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getDate("date"),
                    rs.getString("status"),
                    rs.getString("course")
                });
            }
        } catch (SQLException ex) {
            showError("Error loading attendance: " + ex.getMessage());
        }
    }

    private void filterAttendance() {
        String course = (String) courseComboBox.getSelectedItem();
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT a.student_id, s.name, a.date, a.status, s.course " +
                 "FROM attendance a JOIN students s ON a.student_id = s.id " +
                 "WHERE (? = 'All Courses' OR s.course = ?) ORDER BY a.date DESC")) {
            ps.setString(1, course);
            ps.setString(2, course);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getDate("date"),
                    rs.getString("status"),
                    rs.getString("course")
                });
            }
        } catch (SQLException ex) {
            showError("Error filtering: " + ex.getMessage());
        }
    }

    private void loadStudents() {
        studentModel.setRowCount(0);
        String course = (String) courseComboBox.getSelectedItem();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT student_id, name FROM students " +
                 "WHERE ? = 'All Courses' OR course = ? ORDER BY name")) {
            ps.setString(1, course);
            ps.setString(2, course);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studentModel.addRow(new Object[]{
                    false, 
                    rs.getString("student_id"), 
                    rs.getString("name"), 
                    "Present" // Default status
                });
            }
        } catch (SQLException ex) {
            showError("Error loading students: " + ex.getMessage());
        }
    }

    private void saveAttendance() {
        String dateStr = (String) dateComboBox.getSelectedItem();
        LocalDate date;
        
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            showError("Please enter a valid date in YYYY-MM-DD format");
            return;
        }
        
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement("SELECT COUNT(*) FROM attendance WHERE student_id = ? AND date = ?");
             PreparedStatement insertPs = conn.prepareStatement("INSERT INTO attendance (student_id, date, status) VALUES (?, ?, ?)")) {

            conn.setAutoCommit(false);
            for (int i = 0; i < studentModel.getRowCount(); i++) {
                Boolean selected = (Boolean) studentModel.getValueAt(i, 0);
                if (selected != null && selected) {
                    String studentId = (String) studentModel.getValueAt(i, 1);
                    String status = (String) studentModel.getValueAt(i, 3);
                    
                    // Check if attendance already exists for this student on this date
                    checkPs.setString(1, studentId);
                    checkPs.setDate(2, Date.valueOf(date));
                    ResultSet rs = checkPs.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        int option = JOptionPane.showConfirmDialog(this, 
                            "Attendance already exists for student " + studentId + " on " + date + 
                            ". Overwrite?", "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
                        if (option != JOptionPane.YES_OPTION) continue;
                        
                        // Delete existing record
                        PreparedStatement deletePs = conn.prepareStatement("DELETE FROM attendance WHERE student_id = ? AND date = ?");
                        deletePs.setString(1, studentId);
                        deletePs.setDate(2, Date.valueOf(date));
                        deletePs.executeUpdate();
                    }
                    
                    // Insert new record
                    insertPs.setString(1, studentId);
                    insertPs.setDate(2, Date.valueOf(date));
                    insertPs.setString(3, status);
                    insertPs.executeUpdate();
                    count++;
                }
            }
            conn.commit();
            JOptionPane.showMessageDialog(this, "Attendance saved for " + count + " students", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAttendance();
        } catch (SQLException ex) {
            showError("Error saving attendance: " + ex.getMessage());
        }
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Attendance Data");
        fileChooser.setSelectedFile(new java.io.File("attendance_export.csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (java.io.PrintWriter pw = new java.io.PrintWriter(file);
                 Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT a.student_id, s.name, a.date, a.status, s.course FROM attendance a JOIN students s ON a.student_id = s.id ORDER BY a.date DESC")) {
                pw.println("Student ID,Name,Date,Status,Course");
                while (rs.next()) {
                    pw.println(rs.getString("student_id") + ",\"" + rs.getString("name") + "\"," + rs.getDate("date") + "," + rs.getString("status") + "," + rs.getString("course"));
                }
                JOptionPane.showMessageDialog(this, "Data exported successfully", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showError("Error exporting data: " + ex.getMessage());
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}