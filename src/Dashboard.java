import javax.swing.*;
import java.awt.*;

public class Dashboard extends BaseFrame {
    public Dashboard(String username) {
        super("Dashboard - Attendance Management System", username);
        showWelcomeContent();
    }

    private void showWelcomeContent() {
        mainContentPanel.removeAll();
        
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel welcomeLabel = new JLabel(" Welcome to Attendance Management System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(PRIMARY_COLOR);
        
        JLabel userLabel = new JLabel("Logged in as: " + currentUser);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(Color.DARK_GRAY);
        
        JLabel infoLabel = new JLabel("Use the sidebar to navigate to different modules");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        infoLabel.setForeground(Color.GRAY);
        
        welcomePanel.add(welcomeLabel, gbc);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 20)), gbc);
        welcomePanel.add(userLabel, gbc);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        welcomePanel.add(infoLabel, gbc);
        
        mainContentPanel.add(welcomePanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
}