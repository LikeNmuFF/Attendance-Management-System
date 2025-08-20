import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentManagement extends BaseFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public StudentManagement(String username) {
        super("Student Management", username);
        initializeUI();
    }

    private void initializeUI() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Student Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchBtn = new JButton(" Search");
        styleButton(searchBtn, SECONDARY_COLOR);
        searchBtn.addActionListener(e -> searchStudents(searchField.getText()));

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "Student ID", "Name", "Email", "Phone", "Course"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.setRowHeight(30);
        studentTable.setSelectionBackground(PRIMARY_COLOR);
        studentTable.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JButton addBtn = createActionButton(" Add Student", PRIMARY_COLOR);
        JButton editBtn = createActionButton(" Edit", SECONDARY_COLOR);
        JButton deleteBtn = createActionButton(" Delete", ACCENT_COLOR);
        JButton refreshBtn = createActionButton(" Refresh", new Color(39, 174, 96));

        addBtn.addActionListener(e -> showStudentForm(null));
        editBtn.addActionListener(e -> editSelectedStudent());
        deleteBtn.addActionListener(e -> deleteSelectedStudent());
        refreshBtn.addActionListener(e -> refreshStudents());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        refreshStudents();
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));

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

    private void refreshStudents() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students ORDER BY id")) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("course")
                });
            }
        } catch (SQLException ex) {
            showError("Error loading students: " + ex.getMessage());
        }
    }

    private void searchStudents(String query) {
        if (query.trim().isEmpty()) {
            refreshStudents();
            return;
        }
        
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT * FROM students WHERE student_id LIKE ? OR name LIKE ? OR email LIKE ? OR course LIKE ?")) {
            
            String searchQuery = "%" + query + "%";
            for (int i = 1; i <= 4; i++) ps.setString(i, searchQuery);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("course")
                });
            }
        } catch (SQLException ex) {
            showError("Error searching: " + ex.getMessage());
        }
    }

    private void showStudentForm(String studentId) {
        JDialog dialog = new JDialog(this, studentId == null ? "Add Student" : "Edit Student", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField courseField = new JTextField();
        
        panel.add(new JLabel("Student ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Course:"));
        panel.add(courseField);
        
        // If editing, load existing data
        if (studentId != null) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM students WHERE student_id = ?")) {
                
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    idField.setText(rs.getString("student_id"));
                    idField.setEditable(false); // Cannot change ID when editing
                    nameField.setText(rs.getString("name"));
                    emailField.setText(rs.getString("email"));
                    phoneField.setText(rs.getString("phone"));
                    courseField.setText(rs.getString("course"));
                }
            } catch (SQLException ex) {
                showError("Error loading student: " + ex.getMessage());
            }
        }
        
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String course = courseField.getText().trim();
            
            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Student ID and Name are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DBConnection.getConnection()) {
                if (studentId == null) {
                    // Add new student
                    try (PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO students (student_id, name, email, phone, course) VALUES (?, ?, ?, ?, ?)")) {
                        
                        ps.setString(1, id);
                        ps.setString(2, name);
                        ps.setString(3, email);
                        ps.setString(4, phone);
                        ps.setString(5, course);
                        
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Student added successfully");
                    }
                } else {
                    // Update existing student
                    try (PreparedStatement ps = conn.prepareStatement(
                         "UPDATE students SET name = ?, email = ?, phone = ?, course = ? WHERE student_id = ?")) {
                        
                        ps.setString(1, name);
                        ps.setString(2, email);
                        ps.setString(3, phone);
                        ps.setString(4, course);
                        ps.setString(5, studentId);
                        
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Student updated successfully");
                    }
                }
                
                dialog.dispose();
                refreshStudents();
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate")) {
                    JOptionPane.showMessageDialog(dialog, "Student ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row >= 0) {
            String studentId = tableModel.getValueAt(row, 1).toString();
            showStudentForm(studentId);
        } else {
            showError("Please select a student to edit");
        }
    }

    private void deleteSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            String studentId = tableModel.getValueAt(row, 1).toString();
            String name = tableModel.getValueAt(row, 2).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete student: " + name + " (" + studentId + ")?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id = ?")) {
                    
                    ps.setInt(1, id);
                    int affected = ps.executeUpdate();
                    
                    if (affected > 0) {
                        JOptionPane.showMessageDialog(this, "Student deleted successfully");
                        refreshStudents();
                    }
                } catch (SQLException ex) {
                    showError("Error deleting student: " + ex.getMessage());
                }
            }
        } else {
            showError("Please select a student to delete");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}