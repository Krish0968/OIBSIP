package com.krish.oibsip.reservation.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.krish.oibsip.reservation.model.Reservation;
import com.krish.oibsip.reservation.model.User;
import com.krish.oibsip.reservation.service.AuthService;
import com.krish.oibsip.reservation.service.ReservationService;
import com.krish.oibsip.reservation.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyBookingsPanel extends JPanel {
    private final AuthService authService;
    private final ReservationService reservationService;
    private final MainFrame parentFrame;

    private JTable tblBookings;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    private JTextField txtSearch;
    private JComboBox<String> cbStatusFilter;
    private JLabel lblEmptyState;
    private JButton btnViewDetails;
    private JButton btnCancelSelected;
    
    private List<Reservation> currentReservations = new ArrayList<>();

    public MyBookingsPanel(MainFrame parentFrame, AuthService authService, ReservationService reservationService) {
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
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("My Bookings");
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("View, search, filter, and inspect your railway reservation tickets.");
        lblSub.setFont(UIConstants.FONT_SUBTITLE);
        lblSub.setForeground(Color.GRAY);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Modern Card Container for Filters and Grid
        JPanel mainCard = new JPanel(new BorderLayout(0, 15));
        mainCard.setBackground(UIConstants.COLOR_BG_CARD);
        mainCard.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        mainCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Filter Bar Panel
        JPanel filterBar = new JPanel(new GridBagLayout());
        filterBar.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 12);

        // Search textfield
        gbc.gridx = 0; gbc.weightx = 1.0;
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search PNR or Passenger name...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtSearch.setPreferredSize(new Dimension(0, UIConstants.CONTROL_HEIGHT));
        txtSearch.setFont(UIConstants.FONT_BODY);
        txtSearch.setBackground(UIConstants.COLOR_INPUT_BG);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        filterBar.add(txtSearch, gbc);

        // Status Filter dropdown
        gbc.gridx = 1; gbc.weightx = 0.0;
        cbStatusFilter = new JComboBox<>(new String[]{"All Statuses", "Confirmed", "Cancelled"});
        cbStatusFilter.setPreferredSize(new Dimension(150, UIConstants.CONTROL_HEIGHT));
        cbStatusFilter.setFont(UIConstants.FONT_BODY);
        filterBar.add(cbStatusFilter, gbc);

        // Refresh button
        gbc.gridx = 2; gbc.insets = new Insets(0, 0, 0, 0);
        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.setPreferredSize(new Dimension(120, UIConstants.CONTROL_HEIGHT));
        btnRefresh.setFont(UIConstants.FONT_BODY_BOLD);
        btnRefresh.addActionListener(e -> refreshData());
        filterBar.add(btnRefresh, gbc);

        mainCard.add(filterBar, BorderLayout.NORTH);

        // Center Table Area or Empty State Card
        String[] columns = {"PNR", "Passenger", "Train No.", "Train Name", "Class", "Journey Date", "Source", "Destination", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblBookings = new JTable(tableModel);
        tblBookings.getTableHeader().setReorderingAllowed(false);
        tblBookings.getTableHeader().setFont(UIConstants.FONT_BODY_BOLD);
        tblBookings.setRowHeight(32);
        tblBookings.setFont(UIConstants.FONT_BODY);
        tblBookings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBookings.setBackground(UIConstants.COLOR_BG_CARD);

        // Row Sorter for search and filter capabilities
        rowSorter = new TableRowSorter<>(tableModel);
        tblBookings.setRowSorter(rowSorter);

        // Status column styling
        tblBookings.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String status = value.toString().toUpperCase();
                    if ("CONFIRMED".equals(status)) {
                        c.setForeground(UIConstants.COLOR_SUCCESS);
                        if (!isSelected) {
                            c.setBackground(UIConstants.BG_SUCCESS_MUTED);
                        }
                    } else if ("CANCELLED".equals(status)) {
                        c.setForeground(UIConstants.COLOR_DANGER);
                        if (!isSelected) {
                            c.setBackground(UIConstants.BG_DANGER_MUTED);
                        }
                    } else {
                        c.setForeground(table.getForeground());
                        if (!isSelected) {
                            c.setBackground(table.getBackground());
                        }
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                c.setFont(UIConstants.FONT_BODY_BOLD);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblBookings);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1));
        
        // Empty State card
        lblEmptyState = new JLabel("You do not have any railway bookings.", JLabel.CENTER);
        lblEmptyState.setFont(UIConstants.FONT_MUTED);
        lblEmptyState.setForeground(Color.GRAY);
        lblEmptyState.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(50, 20, 50, 20)
        ));

        // Layered panel to hold table or empty state
        JPanel gridTogglePanel = new JPanel(new CardLayout());
        gridTogglePanel.setOpaque(false);
        gridTogglePanel.add(scrollPane, "Table");
        gridTogglePanel.add(lblEmptyState, "Empty");
        
        mainCard.add(gridTogglePanel, BorderLayout.CENTER);
        add(mainCard, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionBar.setOpaque(false);

        btnViewDetails = new JButton("View Booking Details");
        btnViewDetails.setPreferredSize(new Dimension(180, UIConstants.CONTROL_HEIGHT));
        btnViewDetails.setFont(UIConstants.FONT_BODY_BOLD);
        btnViewDetails.setEnabled(false);
        btnViewDetails.addActionListener(e -> showDetailsOfSelected());

        btnCancelSelected = new JButton("Cancel Selected Ticket");
        btnCancelSelected.setPreferredSize(new Dimension(195, UIConstants.CONTROL_HEIGHT));
        btnCancelSelected.setFont(UIConstants.FONT_BODY_BOLD);
        btnCancelSelected.putClientProperty(FlatClientProperties.STYLE, "background: $lighten(@background, 5%); foreground: #ef5350");
        btnCancelSelected.setEnabled(false);
        btnCancelSelected.addActionListener(e -> cancelSelected());

        actionBar.add(btnViewDetails);
        actionBar.add(btnCancelSelected);
        add(actionBar, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        cbStatusFilter.addActionListener(e -> applyFilters());

        tblBookings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblBookings.getSelectedRow() != -1) {
                    showDetailsOfSelected();
                }
            }
        });

        tblBookings.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = tblBookings.getSelectedRow() != -1;
            btnViewDetails.setEnabled(selected);
            btnCancelSelected.setEnabled(selected);
        });
    }

    public void refreshData() {
        User user = authService.getCurrentUser();
        if (user == null) return;

        tblBookings.clearSelection();
        btnViewDetails.setEnabled(false);
        btnCancelSelected.setEnabled(false);

        try {
            currentReservations = reservationService.getBookingsForUser(user.getId());
            tableModel.setRowCount(0);
            
            for (Reservation res : currentReservations) {
                tableModel.addRow(new Object[]{
                        res.getPnr(),
                        res.getPassengerName(),
                        res.getTrainNumber(),
                        res.getTrainName(),
                        res.getClassType(),
                        DateUtil.formatLocalDateForDisplay(res.getJourneyDate()),
                        res.getSourceStation(),
                        res.getDestinationStation(),
                        res.getBookingStatus()
                });
            }

            CardLayout cl = (CardLayout) lblEmptyState.getParent().getLayout();
            if (currentReservations.isEmpty()) {
                cl.show(lblEmptyState.getParent(), "Empty");
            } else {
                cl.show(lblEmptyState.getParent(), "Table");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load bookings: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        String query = txtSearch.getText().trim();
        String status = (String) cbStatusFilter.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!query.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + query, 0, 1));
        }

        if (status != null && !"All Statuses".equals(status)) {
            filters.add(RowFilter.regexFilter("(?i)^" + status + "$", 8));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void showDetailsOfSelected() {
        int viewRow = tblBookings.getSelectedRow();
        if (viewRow == -1) return;
        
        int modelRow = tblBookings.convertRowIndexToModel(viewRow);
        String pnr = (String) tableModel.getValueAt(modelRow, 0);

        Reservation selected = currentReservations.stream()
                .filter(r -> r.getPnr().equals(pnr))
                .findFirst()
                .orElse(null);

        if (selected == null) return;

        JPanel detailsPanel = new JPanel(new GridLayout(8, 2, 8, 8));
        detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        detailsPanel.add(new JLabel("PNR Number:"));
        JLabel pnrLabel = new JLabel(selected.getPnr()); pnrLabel.setFont(UIConstants.FONT_BODY_BOLD);
        detailsPanel.add(pnrLabel);

        detailsPanel.add(new JLabel("Passenger Name:"));
        detailsPanel.add(new JLabel(selected.getPassengerName()));

        detailsPanel.add(new JLabel("Train:"));
        detailsPanel.add(new JLabel(selected.getTrainNumber() + " - " + selected.getTrainName()));

        detailsPanel.add(new JLabel("Class:"));
        detailsPanel.add(new JLabel(selected.getClassType()));

        detailsPanel.add(new JLabel("Journey Date:"));
        detailsPanel.add(new JLabel(DateUtil.formatLocalDateForDisplay(selected.getJourneyDate())));

        detailsPanel.add(new JLabel("Route:"));
        detailsPanel.add(new JLabel(selected.getSourceStation() + " to " + selected.getDestinationStation()));

        detailsPanel.add(new JLabel("Booked At:"));
        detailsPanel.add(new JLabel(DateUtil.formatLocalDateTimeForDisplay(selected.getBookedAt())));

        detailsPanel.add(new JLabel("Status:"));
        JLabel statusLabel = new JLabel(selected.getBookingStatus());
        statusLabel.setFont(UIConstants.FONT_BODY_BOLD);
        if ("CONFIRMED".equalsIgnoreCase(selected.getBookingStatus())) {
            statusLabel.setForeground(UIConstants.COLOR_SUCCESS);
        } else {
            statusLabel.setForeground(UIConstants.COLOR_DANGER);
        }
        detailsPanel.add(statusLabel);

        JOptionPane.showMessageDialog(this, detailsPanel, "Reservation Details", JOptionPane.PLAIN_MESSAGE);
    }

    private void cancelSelected() {
        int viewRow = tblBookings.getSelectedRow();
        if (viewRow == -1) return;

        int modelRow = tblBookings.convertRowIndexToModel(viewRow);
        String pnr = (String) tableModel.getValueAt(modelRow, 0);

        Reservation selected = currentReservations.stream()
                .filter(r -> r.getPnr().equals(pnr))
                .findFirst()
                .orElse(null);

        if (selected == null) return;

        if ("CANCELLED".equalsIgnoreCase(selected.getBookingStatus())) {
            JOptionPane.showMessageDialog(this, "This ticket is already cancelled.",
                    "Cancellation Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to cancel PNR: " + selected.getPnr() + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) return;

        try {
            reservationService.cancelReservation(selected.getPnr(), authService.getCurrentUser().getId());
            JOptionPane.showMessageDialog(this, "Ticket cancelled successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cancellation failed: " + ex.getMessage(),
                    "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
