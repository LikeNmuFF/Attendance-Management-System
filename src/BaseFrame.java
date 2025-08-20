import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class BaseFrame extends JFrame {
    protected static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    protected static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    protected static final Color ACCENT_COLOR = new Color(231, 76, 60);
    protected static final Color TEXT_COLOR = new Color(236, 240, 241);
    protected static final Color BG_COLOR = new Color(44, 62, 80);
    protected static final Color PANEL_COLOR = new Color(52, 73, 94);
    protected static final Color SIDEBAR_COLOR = new Color(33, 47, 61);
    protected static final Color CONTENT_BG = new Color(240, 240, 240);

    protected JPanel mainContentPanel;
    protected String currentUser;

    public BaseFrame(String title, String username) {
        super(title);
        this.currentUser = username;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setupUI();
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        JPanel sidebarPanel = createSidebar();
        mainContentPanel = createContentPanel();

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    protected JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CONTENT_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return contentPanel;
    }

    protected JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);

        JLabel logoLabel = new JLabel();
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(TEXT_COLOR);
        logoPanel.add(logoLabel);

        String fullText = "   Attendance   Management  System   ";
        final String[] text = {fullText};

        Timer marqueeTimer = new Timer(150, e -> {
            text[0] = text[0].substring(1) + text[0].charAt(0);
            logoLabel.setText(text[0]);
        });
        marqueeTimer.start();

        JPanel userPanel = createUserPanel();

        JButton dashboardBtn = createSidebarButton(" Dashboard");
        JButton studentBtn = createSidebarButton(" Student");
        JButton attendanceBtn = createSidebarButton(" Attendance");
        JButton reportBtn = createSidebarButton(" Reports");
        JButton exportBtn = createSidebarButton(" Export Data");

        dashboardBtn.addActionListener(e -> openDashboard());
        studentBtn.addActionListener(e -> openStudentManagement());
        attendanceBtn.addActionListener(e -> openAttendanceSystem());
        reportBtn.addActionListener(e -> openReports());
        exportBtn.addActionListener(e -> openExport());

        JPanel logoutPanel = createLogoutPanel();

        sidebarPanel.add(logoPanel);
        sidebarPanel.add(userPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(dashboardBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(studentBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(attendanceBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(reportBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(exportBtn);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(logoutPanel);

        return sidebarPanel;
    }

    protected JPanel createUserPanel() {
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 30, 10));

        ImageIcon profileIcon = new ImageIcon("icons/school.png");
        if (profileIcon.getIconWidth() > 0 && profileIcon.getIconHeight() > 0) {
            Image scaledImg = profileIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            ImageIcon circularIcon = makeCircularImage(new ImageIcon(scaledImg));
            JLabel profileLabel = new JLabel(circularIcon);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            JLabel userLabel = new JLabel(currentUser);
            userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            userLabel.setForeground(TEXT_COLOR);

            JLabel roleLabel = new JLabel("Administrator");
            roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            roleLabel.setForeground(new Color(189, 195, 199));

            textPanel.add(userLabel);
            textPanel.add(roleLabel);

            userPanel.add(profileLabel);
            userPanel.add(textPanel);
        }

        return userPanel;
    }

    protected ImageIcon makeCircularImage(ImageIcon icon) {
        int size = Math.min(icon.getIconWidth(), icon.getIconHeight());
        BufferedImage masked = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = masked.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new Ellipse2D.Float(0, 0, size, size));
        g2.drawImage(icon.getImage(), 0, 0, size, size, null);
        g2.dispose();
        return new ImageIcon(masked);
    }

    protected JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(SIDEBAR_COLOR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(220, 45));
        button.setMaximumSize(new Dimension(220, 45));
        button.setMinimumSize(new Dimension(220, 45));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PANEL_COLOR);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SIDEBAR_COLOR);
            }
        });

        return button;
    }

    protected JPanel createLogoutPanel() {
        JPanel logoutPanel = new JPanel();
        logoutPanel.setOpaque(false);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton logoutBtn = new JButton(" Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(ACCENT_COLOR);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setPreferredSize(new Dimension(180, 40));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(ACCENT_COLOR.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(ACCENT_COLOR);
            }
        });

        logoutBtn.addActionListener(e -> logout());
        logoutPanel.add(logoutBtn);

        return logoutPanel;
    }

    protected void openDashboard() { new Dashboard(currentUser).setVisible(true); dispose(); }
    protected void openStudentManagement() { new StudentManagement(currentUser).setVisible(true); dispose(); }
    protected void openAttendanceSystem() { new AttendanceSystem(currentUser).setVisible(true); dispose(); }
    protected void openReports() { new Report(currentUser).setVisible(true); dispose(); }
    protected void openExport() { new Export(currentUser).setVisible(true); dispose(); }

    protected void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Pasado naba ako sir?",
                "Created by: Justin Barutag",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new Login().setVisible(true);
        }
    }
}
        