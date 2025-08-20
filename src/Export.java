
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.sql.*;
import javax.swing.*;

public class Export extends BaseFrame {
    private JComboBox<String> formatCombo;
    private JComboBox<String> dataTypeCombo;

    public Export(String username) {
        super("Export Data", username);
        initializeUI();
    }

    private void initializeUI() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(" Data Export");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel exportLabel = new JLabel("Export Data");
        exportLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        exportLabel.setForeground(PRIMARY_COLOR);

        JLabel formatLabel = new JLabel("Export format:");
        formatLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        formatCombo = new JComboBox<>(new String[]{"CSV", "Excel (CSV)"});
        formatCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel dataTypeLabel = new JLabel("Data to export:");
        dataTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        dataTypeCombo = new JComboBox<>(new String[]{"Students", "Attendance Records"});
        dataTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton exportBtn = new JButton(" Export Now");
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setBackground(new Color(39, 174, 96));
        exportBtn.setFocusPainted(false);
        exportBtn.setBorderPainted(false);
        exportBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exportBtn.setPreferredSize(new Dimension(150, 40));

        exportBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { exportBtn.setBackground(new Color(46, 204, 113)); }
            public void mouseExited(MouseEvent e) { exportBtn.setBackground(new Color(39, 174, 96)); }
        });

        exportBtn.addActionListener(e -> exportData());

        contentPanel.add(exportLabel, gbc);
        contentPanel.add(formatLabel, gbc);
        contentPanel.add(formatCombo, gbc);
        contentPanel.add(dataTypeLabel, gbc);
        contentPanel.add(dataTypeCombo, gbc);
        contentPanel.add(exportBtn, gbc);

        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void exportData() {
        String format = (String) formatCombo.getSelectedItem();
        String dataType = (String) dataTypeCombo.getSelectedItem();
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Export File");
        
        String defaultFileName = dataType.toLowerCase().replace(" ", "_") + "_export." + 
                               (format.equals("CSV") ? "csv" : "xlsx");
        fileChooser.setSelectedFile(new java.io.File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            
            try {
                if (dataType.equals("Students")) {
                    exportStudents(file, format);
                } else {
                    exportAttendance(file, format);
                }
                
                JOptionPane.showMessageDialog(this, 
                    dataType + " exported successfully to " + file.getName(), 
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting data: " + ex.getMessage(), 
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportStudents(java.io.File file, String format) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students ORDER BY student_id");
             FileWriter writer = new FileWriter(file)) {
            
            // Write header
            writer.write("ID,Student ID,Name,Email,Phone,Course\n");
            
            // Write data
            while (rs.next()) {
                writer.write(
                    rs.getInt("id") + "," +
                    rs.getString("student_id") + "," +
                    "\"" + rs.getString("name") + "\"," +
                    rs.getString("email") + "," +
                    rs.getString("phone") + "," +
                    rs.getString("course") + "\n"
                );
            }
        }
    }

    private void exportAttendance(java.io.File file, String format) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT a.id, a.student_id, s.name, a.date, a.status, s.course " +
                 "FROM attendance a JOIN students s ON a.student_id = s.id ORDER BY a.date DESC");
             FileWriter writer = new FileWriter(file)) {
            
            // Write header
            writer.write("ID,Student ID,Name,Date,Status,Course\n");
            
            // Write data
            while (rs.next()) {
                writer.write(
                    rs.getInt("id") + "," +
                    rs.getString("student_id") + "," +
                    "\"" + rs.getString("name") + "\"," +
                    rs.getDate("date") + "," +
                    rs.getString("status") + "," +
                    rs.getString("course") + "\n"
                );
            }
        }
    }
}