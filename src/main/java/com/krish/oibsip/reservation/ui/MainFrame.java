package com.krish.oibsip.reservation.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.krish.oibsip.reservation.model.User;
import com.krish.oibsip.reservation.service.AuthService;
import com.krish.oibsip.reservation.service.ReservationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final AuthService authService;
    private final ReservationService reservationService;

    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Sub-panels
    private DashboardPanel dashboardPanel;
    private ReservationPanel reservationPanel;
    private MyBookingsPanel myBookingsPanel;
    private CancellationPanel cancellationPanel;

    // Sidebar navigation buttons tracker
    private final Map<String, JButton> navButtons = new HashMap<>();

    public MainFrame(AuthService authService) {
        this.authService = authService;
        this.reservationService = new ReservationService();

        setTitle("RailNexus - Smart Railway Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setSize(1150, 750);
        setLocationRelativeTo(null); // Center on screen

        initComponents();
        switchPanel("Dashboard");
    }

    private void initComponents() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(UIConstants.COLOR_BG_WINDOW);

        // 1. TOP HEADER PANEL (with distinct background and bottom line border)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 65));
        headerPanel.setBackground(UIConstants.COLOR_BG_SIDEBAR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.COLOR_BORDER),
                new EmptyBorder(10, 25, 10, 25)
        ));

        // Brand Title
        JLabel lblTitle = new JLabel("RAILNEXUS");
        lblTitle.setFont(new Font(UIConstants.FONT_FAMILY, Font.BOLD, 20));
        lblTitle.setForeground(UIConstants.COLOR_PRIMARY);
        
        JLabel lblSubtitle = new JLabel("  Smart Railway Reservation System");
        lblSubtitle.setFont(UIConstants.FONT_MUTED);
        lblSubtitle.setForeground(Color.GRAY);

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        brandPanel.setOpaque(false);
        brandPanel.add(lblTitle);
        brandPanel.add(lblSubtitle);
        headerPanel.add(brandPanel, BorderLayout.WEST);

        // User Session Info (No glyphs/icons for clean portfolio look)
        JPanel sessionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 6));
        sessionPanel.setOpaque(false);

        User currentUser = authService.getCurrentUser();
        String name = currentUser != null ? currentUser.getFullName() : "Guest User";
        JLabel lblUser = new JLabel("Logged in: " + name);
        lblUser.setFont(UIConstants.FONT_BODY_BOLD);
        lblUser.setForeground(Color.LIGHT_GRAY);

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd-MMM-yyyy"));
        JLabel lblDate = new JLabel(dateStr);
        lblDate.setFont(UIConstants.FONT_MUTED);
        lblDate.setForeground(Color.GRAY);

        sessionPanel.add(lblUser);
        sessionPanel.add(new JSeparator(JSeparator.VERTICAL));
        sessionPanel.add(lblDate);
        headerPanel.add(sessionPanel, BorderLayout.EAST);
        
        mainContent.add(headerPanel, BorderLayout.NORTH);

        // 2. LEFT SIDEBAR PANEL (Distinct obsidian background and right line border)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebarPanel.setBackground(UIConstants.COLOR_BG_SIDEBAR);
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.COLOR_BORDER),
                new EmptyBorder(25, 12, 25, 12)
        ));

        // Navigation buttons
        addNavButton(sidebarPanel, "Dashboard", "Dashboard");
        addNavButton(sidebarPanel, "Book Ticket", "Book Ticket");
        addNavButton(sidebarPanel, "My Bookings", "My Bookings");
        addNavButton(sidebarPanel, "Cancel Reservation", "Cancel Reservation");
        
        sidebarPanel.add(Box.createVerticalGlue()); // Spacer

        // Sidebar Logout Button
        JButton btnSidebarLogout = new JButton("Logout Session");
        btnSidebarLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSidebarLogout.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 24, UIConstants.CONTROL_HEIGHT + 4));
        btnSidebarLogout.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 24, UIConstants.CONTROL_HEIGHT + 4));
        btnSidebarLogout.setFont(UIConstants.FONT_BODY_BOLD);
        btnSidebarLogout.putClientProperty(FlatClientProperties.STYLE, "background: #c62828; foreground: #ffffff; arc: 8");
        btnSidebarLogout.addActionListener(e -> handleLogout());
        sidebarPanel.add(btnSidebarLogout);

        mainContent.add(sidebarPanel, BorderLayout.WEST);

        // 3. CENTER CONTENT CONTAINER (CardLayout on dark window canvas)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UIConstants.COLOR_BG_WINDOW);
        cardPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Instantiate sub panels
        dashboardPanel = new DashboardPanel(this, authService, reservationService);
        reservationPanel = new ReservationPanel(this, authService, reservationService);
        myBookingsPanel = new MyBookingsPanel(this, authService, reservationService);
        cancellationPanel = new CancellationPanel(this, authService, reservationService);

        // Add to card layout
        cardPanel.add(dashboardPanel, "Dashboard");
        cardPanel.add(reservationPanel, "Book Ticket");
        cardPanel.add(myBookingsPanel, "My Bookings");
        cardPanel.add(cancellationPanel, "Cancel Reservation");

        mainContent.add(cardPanel, BorderLayout.CENTER);
        setContentPane(mainContent);
    }

    private void addNavButton(JPanel container, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BODY_BOLD);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 24, UIConstants.CONTROL_HEIGHT + 6));
        btn.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 24, UIConstants.CONTROL_HEIGHT + 6));
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        btn.addActionListener(e -> switchPanel(cardName));
        
        container.add(btn);
        container.add(Box.createRigidArea(new Dimension(0, 12))); // Spacing
        
        navButtons.put(cardName, btn);
    }

    /**
     * Switched active Panel in CardLayout.
     * Triggers child refresh routines to load accurate up-to-date data.
     */
    public void switchPanel(String cardName) {
        cardLayout.show(cardPanel, cardName);

        // Refresh panel data
        if ("Dashboard".equals(cardName)) {
            dashboardPanel.refreshData();
        } else if ("Book Ticket".equals(cardName)) {
            reservationPanel.loadTrains();
        } else if ("My Bookings".equals(cardName)) {
            myBookingsPanel.refreshData();
        } else if ("Cancel Reservation".equals(cardName)) {
            cancellationPanel.resetPanel();
        }

        updateSidebarSelection(cardName);
    }

    private void updateSidebarSelection(String activeCard) {
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(activeCard)) {
                btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, "primary");
                btn.setBackground(UIConstants.COLOR_PRIMARY);
                btn.setForeground(Color.WHITE);
            } else {
                btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, null);
                btn.setBackground(UIConstants.COLOR_INPUT_BG);
                btn.setForeground(Color.LIGHT_GRAY);
            }
        }
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to log out of your session?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            authService.logout();
            new LoginFrame(authService).setVisible(true);
            dispose();
        }
    }
}
