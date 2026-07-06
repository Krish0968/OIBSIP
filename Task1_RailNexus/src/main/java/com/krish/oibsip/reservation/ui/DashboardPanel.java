package com.krish.oibsip.reservation.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.krish.oibsip.reservation.model.Reservation;
import com.krish.oibsip.reservation.model.User;
import com.krish.oibsip.reservation.service.AuthService;
import com.krish.oibsip.reservation.service.ReservationService;
import com.krish.oibsip.reservation.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final AuthService authService;
    private final ReservationService reservationService;
    private final MainFrame parentFrame;

    private JLabel lblWelcome;
    private JLabel lblTotalBookings;
    private JLabel lblConfirmedBookings;
    private JLabel lblCancelledBookings;
    private JTable tblRecentBookings;
    private DefaultTableModel tableModel;

    public DashboardPanel(MainFrame parentFrame, AuthService authService, ReservationService reservationService) {
        this.parentFrame = parentFrame;
        this.authService = authService;
        this.reservationService = reservationService;

        setBackground(UIConstants.COLOR_BG_WINDOW);
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // Welcome Header (Clean, styled)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        lblWelcome = new JLabel("Welcome back, User");
        lblWelcome.setFont(UIConstants.FONT_TITLE);
        lblWelcome.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Here is a summary of your railway bookings.");
        lblSub.setFont(UIConstants.FONT_SUBTITLE);
        lblSub.setForeground(Color.GRAY);

        headerPanel.add(lblWelcome, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel holding stats and bookings card
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        // 1. Stats Cards Grid
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 15, 0));
        statsGrid.setOpaque(false);
        statsGrid.add(createCard("Total Bookings", "0", UIConstants.COLOR_PRIMARY));
        statsGrid.add(createCard("Confirmed", "0", UIConstants.COLOR_SUCCESS));
        statsGrid.add(createCard("Cancelled", "0", UIConstants.COLOR_DANGER));
        centerPanel.add(statsGrid, BorderLayout.NORTH);

        // 2. Modern Table Card (Replaces titled border with structured panel)
        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UIConstants.COLOR_BG_CARD);
        tableCard.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTableTitle = new JLabel("Recent Bookings");
        lblTableTitle.setFont(UIConstants.FONT_SECTION);
        lblTableTitle.setForeground(Color.WHITE);
        tableCard.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"PNR", "Passenger", "Train", "Journey Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblRecentBookings = new JTable(tableModel);
        tblRecentBookings.getTableHeader().setReorderingAllowed(false);
        tblRecentBookings.getTableHeader().setFont(UIConstants.FONT_BODY_BOLD);
        tblRecentBookings.setRowHeight(32);
        tblRecentBookings.setFont(UIConstants.FONT_BODY);
        tblRecentBookings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRecentBookings.setBackground(UIConstants.COLOR_BG_CARD);

        JScrollPane scrollPane = new JScrollPane(tblRecentBookings);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tableCard, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 3. Quick Actions Card
        JPanel quickActionsCard = new JPanel(new BorderLayout(15, 0));
        quickActionsCard.setBackground(UIConstants.COLOR_BG_CARD);
        quickActionsCard.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        quickActionsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblActionsTitle = new JLabel("Quick Actions");
        lblActionsTitle.setFont(UIConstants.FONT_SECTION);
        lblActionsTitle.setForeground(Color.WHITE);
        quickActionsCard.add(lblActionsTitle, BorderLayout.WEST);

        JPanel actionsButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsButtonPanel.setOpaque(false);

        JButton btnBook = new JButton("Book New Ticket");
        btnBook.putClientProperty(FlatClientProperties.BUTTON_TYPE, "primary");
        btnBook.setPreferredSize(new Dimension(160, UIConstants.CONTROL_HEIGHT));
        btnBook.setFont(UIConstants.FONT_BODY_BOLD);
        btnBook.addActionListener(e -> parentFrame.switchPanel("Book Ticket"));

        JButton btnView = new JButton("View My Bookings");
        btnView.setPreferredSize(new Dimension(160, UIConstants.CONTROL_HEIGHT));
        btnView.setFont(UIConstants.FONT_BODY_BOLD);
        btnView.addActionListener(e -> parentFrame.switchPanel("My Bookings"));

        JButton btnCancel = new JButton("Cancel Reservation");
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "background: $lighten(@background, 5%); foreground: #ef5350");
        btnCancel.setPreferredSize(new Dimension(170, UIConstants.CONTROL_HEIGHT));
        btnCancel.setFont(UIConstants.FONT_BODY_BOLD);
        btnCancel.addActionListener(e -> parentFrame.switchPanel("Cancel Reservation"));

        actionsButtonPanel.add(btnView);
        actionsButtonPanel.add(btnCancel);
        actionsButtonPanel.add(btnBook);
        quickActionsCard.add(actionsButtonPanel, BorderLayout.CENTER);

        add(quickActionsCard, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, String defaultValue, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(UIConstants.COLOR_BG_CARD);
        card.setPreferredSize(new Dimension(0, 120));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                        BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1)
                ),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(UIConstants.FONT_CARD_TITLE);
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(defaultValue);
        lblValue.setFont(UIConstants.FONT_CARD_VALUE);
        lblValue.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        if (title.equals("Total Bookings")) {
            lblTotalBookings = lblValue;
        } else if (title.equals("Confirmed")) {
            lblConfirmedBookings = lblValue;
        } else if (title.equals("Cancelled")) {
            lblCancelledBookings = lblValue;
        }

        return card;
    }

    public void refreshData() {
        User user = authService.getCurrentUser();
        if (user == null) return;

        lblWelcome.setText("Welcome back, " + user.getFullName());

        try {
            List<Reservation> bookings = reservationService.getBookingsForUser(user.getId());
            
            int total = bookings.size();
            int confirmed = 0;
            int cancelled = 0;
            
            for (Reservation res : bookings) {
                if ("CONFIRMED".equalsIgnoreCase(res.getBookingStatus())) {
                    confirmed++;
                } else if ("CANCELLED".equalsIgnoreCase(res.getBookingStatus())) {
                    cancelled++;
                }
            }

            lblTotalBookings.setText(String.valueOf(total));
            lblConfirmedBookings.setText(String.valueOf(confirmed));
            lblCancelledBookings.setText(String.valueOf(cancelled));

            tableModel.setRowCount(0);
            int count = 0;
            for (Reservation res : bookings) {
                if (count >= 5) break;
                
                String trainInfo = res.getTrainNumber() + " - " + res.getTrainName();
                tableModel.addRow(new Object[]{
                        res.getPnr(),
                        res.getPassengerName(),
                        trainInfo,
                        DateUtil.formatLocalDateForDisplay(res.getJourneyDate()),
                        res.getBookingStatus()
                });
                count++;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                    "Error loading dashboard metrics: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
