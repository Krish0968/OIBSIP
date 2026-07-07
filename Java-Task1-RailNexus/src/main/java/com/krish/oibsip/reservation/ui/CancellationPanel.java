package com.krish.oibsip.reservation.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.krish.oibsip.reservation.model.Reservation;
import com.krish.oibsip.reservation.model.User;
import com.krish.oibsip.reservation.service.AuthService;
import com.krish.oibsip.reservation.service.ReservationService;
import com.krish.oibsip.reservation.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class CancellationPanel extends JPanel {
    private final AuthService authService;
    private final ReservationService reservationService;
    private final MainFrame parentFrame;

    private JTextField txtPnrSearch;
    private JButton btnSearch;
    private JPanel detailsContainer;
    private JButton btnCancelTicket;

    private JPanel contentCardPanel;
    private JPanel placeholderPanel;

    // Detail labels
    private JLabel lblPnrVal;
    private JLabel lblPassengerVal;
    private JLabel lblTrainVal;
    private JLabel lblClassVal;
    private JLabel lblDateVal;
    private JLabel lblRouteVal;
    private JLabel lblStatusVal;
    private JLabel lblBookedAtVal;

    private Reservation activeReservation = null;

    public CancellationPanel(MainFrame parentFrame, AuthService authService, ReservationService reservationService) {
        this.parentFrame = parentFrame;
        this.authService = authService;
        this.reservationService = reservationService;

        setBackground(UIConstants.COLOR_BG_WINDOW);
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        setupEvents();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Cancel Railway Reservation");
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Enter your PNR code to retrieve booking details and process ticket cancellation.");
        lblSub.setFont(UIConstants.FONT_SUBTITLE);
        lblSub.setForeground(Color.GRAY);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center content holding card panel
        JPanel centerContent = new JPanel(new BorderLayout(0, 15));
        centerContent.setOpaque(false);

        // 1. Search Booking card
        JPanel searchCard = new JPanel(new GridBagLayout());
        searchCard.setBackground(UIConstants.COLOR_BG_CARD);
        searchCard.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        searchCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 15);

        gbc.gridx = 0; gbc.weightx = 1.0;
        txtPnrSearch = new JTextField();
        txtPnrSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter unique PNR (e.g. RNX-20260706-A7K9Q2)");
        txtPnrSearch.setPreferredSize(new Dimension(0, UIConstants.CONTROL_HEIGHT));
        txtPnrSearch.setFont(UIConstants.FONT_BODY);
        txtPnrSearch.setBackground(UIConstants.COLOR_INPUT_BG);
        txtPnrSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        searchCard.add(txtPnrSearch, gbc);

        gbc.gridx = 1; gbc.weightx = 0.0; gbc.insets = new Insets(0, 0, 0, 0);
        btnSearch = new JButton("Fetch Ticket Details");
        btnSearch.setPreferredSize(new Dimension(180, UIConstants.CONTROL_HEIGHT));
        btnSearch.setFont(UIConstants.FONT_BODY_BOLD);
        btnSearch.putClientProperty(FlatClientProperties.BUTTON_TYPE, "primary");
        btnSearch.setBackground(UIConstants.COLOR_PRIMARY);
        btnSearch.setForeground(Color.WHITE);
        searchCard.add(btnSearch, gbc);

        centerContent.add(searchCard, BorderLayout.NORTH);

        // 2. CardLayout container for details vs placeholder empty state
        contentCardPanel = new JPanel(new CardLayout());
        contentCardPanel.setOpaque(false);

        // Placeholder Empty State panel
        placeholderPanel = new JPanel(new GridBagLayout());
        placeholderPanel.setBackground(UIConstants.COLOR_BG_CARD);
        placeholderPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        placeholderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(60, 20, 60, 20)
        ));
        
        JLabel lblPlaceholder = new JLabel("Enter a PNR code above to retrieve and review your reservation details.");
        lblPlaceholder.setFont(UIConstants.FONT_MUTED);
        lblPlaceholder.setForeground(Color.GRAY);
        placeholderPanel.add(lblPlaceholder);

        // Booking details card container
        detailsContainer = new JPanel(new BorderLayout(0, 15));
        detailsContainer.setBackground(UIConstants.COLOR_BG_CARD);
        detailsContainer.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        detailsContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Details grid layout
        JPanel infoGrid = new JPanel(new GridLayout(4, 4, 15, 15));
        infoGrid.setOpaque(false);

        // Row 1
        infoGrid.add(createLabel("PNR Number:"));
        lblPnrVal = createValueLabel();
        infoGrid.add(lblPnrVal);
        
        infoGrid.add(createLabel("Passenger:"));
        lblPassengerVal = createValueLabel();
        infoGrid.add(lblPassengerVal);

        // Row 2
        infoGrid.add(createLabel("Train Details:"));
        lblTrainVal = createValueLabel();
        infoGrid.add(lblTrainVal);

        infoGrid.add(createLabel("Class Type:"));
        lblClassVal = createValueLabel();
        infoGrid.add(lblClassVal);

        // Row 3
        infoGrid.add(createLabel("Journey Date:"));
        lblDateVal = createValueLabel();
        infoGrid.add(lblDateVal);

        infoGrid.add(createLabel("Route Stations:"));
        lblRouteVal = createValueLabel();
        infoGrid.add(lblRouteVal);

        // Row 4
        infoGrid.add(createLabel("Booked Timestamp:"));
        lblBookedAtVal = createValueLabel();
        infoGrid.add(lblBookedAtVal);

        infoGrid.add(createLabel("Booking Status:"));
        lblStatusVal = new JLabel("-");
        lblStatusVal.setFont(UIConstants.FONT_BODY_BOLD);
        infoGrid.add(lblStatusVal);

        detailsContainer.add(infoGrid, BorderLayout.CENTER);

        // Confirm cancellation action button
        JPanel cancelActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        cancelActionPanel.setOpaque(false);
        
        btnCancelTicket = new JButton("Confirm Cancellation");
        btnCancelTicket.setPreferredSize(new Dimension(200, UIConstants.CONTROL_HEIGHT));
        btnCancelTicket.setFont(UIConstants.FONT_BODY_BOLD);
        btnCancelTicket.putClientProperty(FlatClientProperties.STYLE, "background: #c62828; foreground: #ffffff; arc: 8");
        cancelActionPanel.add(btnCancelTicket);

        detailsContainer.add(cancelActionPanel, BorderLayout.SOUTH);

        contentCardPanel.add(placeholderPanel, "Placeholder");
        contentCardPanel.add(detailsContainer, "Details");

        centerContent.add(contentCardPanel, BorderLayout.CENTER);
        add(centerContent, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_BODY_BOLD);
        lbl.setForeground(Color.LIGHT_GRAY);
        return lbl;
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("-");
        lbl.setFont(UIConstants.FONT_BODY);
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private void setupEvents() {
        btnSearch.addActionListener(e -> fetchPnrDetails());
        btnCancelTicket.addActionListener(e -> handleCancellation());
        txtPnrSearch.addActionListener(e -> fetchPnrDetails());
    }

    public void resetPanel() {
        txtPnrSearch.setText("");
        activeReservation = null;
        CardLayout cl = (CardLayout) contentCardPanel.getLayout();
        cl.show(contentCardPanel, "Placeholder");
    }

    private void fetchPnrDetails() {
        String pnrInput = txtPnrSearch.getText().trim();
        activeReservation = null;
        CardLayout cl = (CardLayout) contentCardPanel.getLayout();

        if (pnrInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a PNR code.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            cl.show(contentCardPanel, "Placeholder");
            txtPnrSearch.requestFocus();
            return;
        }

        User user = authService.getCurrentUser();
        if (user == null) return;

        try {
            activeReservation = reservationService.getReservationByPnr(pnrInput, user.getId());

            // Populate labels
            lblPnrVal.setText(activeReservation.getPnr());
            lblPassengerVal.setText(activeReservation.getPassengerName());
            lblTrainVal.setText(activeReservation.getTrainNumber() + " - " + activeReservation.getTrainName());
            lblClassVal.setText(activeReservation.getClassType());
            lblDateVal.setText(DateUtil.formatLocalDateForDisplay(activeReservation.getJourneyDate()));
            lblRouteVal.setText(activeReservation.getSourceStation() + " to " + activeReservation.getDestinationStation());
            lblBookedAtVal.setText(DateUtil.formatLocalDateTimeForDisplay(activeReservation.getBookedAt()));
            
            String status = activeReservation.getBookingStatus();
            lblStatusVal.setText(status);
            if ("CONFIRMED".equalsIgnoreCase(status)) {
                lblStatusVal.setForeground(UIConstants.COLOR_SUCCESS);
                btnCancelTicket.setEnabled(true);
                btnCancelTicket.setVisible(true);
            } else {
                lblStatusVal.setForeground(UIConstants.COLOR_DANGER);
                btnCancelTicket.setEnabled(false);
                btnCancelTicket.setVisible(false); // Hide destructive button for cancelled ticket
            }

            cl.show(contentCardPanel, "Details");
            revalidate();
            repaint();

        } catch (IllegalArgumentException ex) {
            cl.show(contentCardPanel, "Placeholder");
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "PNR Search Failed", JOptionPane.WARNING_MESSAGE);
        } catch (SecurityException ex) {
            cl.show(contentCardPanel, "Placeholder");
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Authorization Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            cl.show(contentCardPanel, "Placeholder");
            JOptionPane.showMessageDialog(this, "Error fetching reservation: " + ex.getMessage(),
                    "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancellation() {
        if (activeReservation == null) return;

        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to cancel PNR: " + activeReservation.getPnr() + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            reservationService.cancelReservation(activeReservation.getPnr(), authService.getCurrentUser().getId());
            
            JOptionPane.showMessageDialog(this, 
                    "Your reservation under PNR " + activeReservation.getPnr() + " has been successfully cancelled.",
                    "Cancellation Complete", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload details to show updated cancelled state
            fetchPnrDetails();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cancellation failed: " + ex.getMessage(),
                    "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
