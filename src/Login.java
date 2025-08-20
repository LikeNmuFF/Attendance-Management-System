
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    
    private Color primaryColor = new Color(41, 128, 185);
    private Color accentColor = new Color(231, 76, 60);
    private Color successColor = new Color(46, 204, 113);
    private Color textColor = new Color(236, 240, 241);
    private Color bgColor = new Color(44, 62, 80);
    private Color panelColor = new Color(52, 73, 94);
    
    public Login() {
        setTitle("Attendance Management System - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        setupUI();
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(bgColor);
        
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(panelColor);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        loginPanel.setPreferredSize(new Dimension(400, 300));
        
        // Title
        JLabel titleLabel = new JLabel(" Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(textColor);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        usernameField = new JTextField(15);
        styleTextField(usernameField);
        
        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(textColor);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        passwordField = new JPasswordField(15);
        styleTextField(passwordField);
        
        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(primaryColor);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setPreferredSize(new Dimension(120, 40));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginBtn.setBackground(primaryColor.brighter()); }
            public void mouseExited(MouseEvent e) { loginBtn.setBackground(primaryColor); }
        });
        
        loginBtn.addActionListener(e -> login());
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(textColor);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(userLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        loginPanel.add(usernameField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(passLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(loginBtn);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginPanel.add(statusLabel);
        
        mainPanel.add(loginPanel);
        setContentPane(mainPanel);
        
        // Enter key listener
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) login();
            }
        });
    }
    
    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(200, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setForeground(accentColor);
            statusLabel.setText("Please enter both username and password");
            return;
        }
        
        // Database authentication
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT * FROM users WHERE username = ? AND password = ?")) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                statusLabel.setForeground(successColor);
                statusLabel.setText("Login successful!");
                
                Timer timer = new Timer(1000, e -> {
                    dispose();
                    new Dashboard(username).setVisible(true);
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                statusLabel.setForeground(accentColor);
                statusLabel.setText("Invalid username or password");
            }
        } catch (SQLException ex) {
            statusLabel.setForeground(accentColor);
            statusLabel.setText("Database error: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Login().setVisible(true);
        });
    }
}