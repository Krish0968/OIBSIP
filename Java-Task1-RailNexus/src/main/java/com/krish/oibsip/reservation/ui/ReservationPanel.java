package com.krish.oibsip.reservation.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.krish.oibsip.reservation.model.Reservation;
import com.krish.oibsip.reservation.model.Train;
import com.krish.oibsip.reservation.model.User;
import com.krish.oibsip.reservation.service.AuthService;
import com.krish.oibsip.reservation.service.ReservationService;
import com.krish.oibsip.reservation.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ReservationPanel extends JPanel {
    private final AuthService authService;
    private final ReservationService reservationService;
    private final MainFrame parentFrame;

    private JTextField txtPassengerName;
    private JComboBox<Train> cbTrains;
    private JTextField txtTrainName;
    private JTextField txtSource;
    private JTextField txtDestination;
    private JTextField txtDeparture;
    private JTextField txtArrival;
    private JComboBox<String> cbClassType;
    private JSpinner spinJourneyDate;
    private JButton btnBook;
    private JButton btnClear;

    public ReservationPanel(MainFrame parentFrame, AuthService authService, ReservationService reservationService) {
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

        JLabel lblTitle = new JLabel("Book Train Ticket");
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Enter passenger details and select a train to book your ticket.");
        lblSub.setFont(UIConstants.FONT_SUBTITLE);
        lblSub.setForeground(Color.GRAY);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Modern Form Card Container
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(UIConstants.COLOR_BG_CARD);
        formCard.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.weightx = 0.5;

        // COLUMN 1 (LEFT)
        // Passenger Name
        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(createFormLabel("Passenger Name"), gbc);
        txtPassengerName = createInputField("Enter passenger full name");
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(txtPassengerName, gbc);

        // Train Selection
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(createFormLabel("Select Train (Number)"), gbc);
        cbTrains = new JComboBox<>();
        cbTrains.setPreferredSize(new Dimension(0, UIConstants.CONTROL_HEIGHT));
        cbTrains.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(cbTrains, gbc);

        // Train Name (Read Only)
        gbc.gridx = 0; gbc.gridy = 4;
        formCard.add(createFormLabel("Train Name"), gbc);
        txtTrainName = createReadOnlyField();
        gbc.gridx = 0; gbc.gridy = 5;
        formCard.add(txtTrainName, gbc);

        // Class Type
        gbc.gridx = 0; gbc.gridy = 6;
        formCard.add(createFormLabel("Class Type"), gbc);
        String[] classes = {"Sleeper (SL)", "AC 3 Tier (3A)", "AC 2 Tier (2A)", "AC First Class (1A)"};
        cbClassType = new JComboBox<>(classes);
        cbClassType.setPreferredSize(new Dimension(0, UIConstants.CONTROL_HEIGHT));
        cbClassType.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 7;
        formCard.add(cbClassType, gbc);

        // COLUMN 2 (RIGHT)
        // Journey Date
        gbc.gridx = 1; gbc.gridy = 0;
        formCard.add(createFormLabel("Journey Date"), gbc);
        SpinnerModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        spinJourneyDate = new JSpinner(dateModel);
        JSpinner.DateEditor de = new JSpinner.DateEditor(spinJourneyDate, "yyyy-MM-dd");
        spinJourneyDate.setEditor(de);
        spinJourneyDate.setPreferredSize(new Dimension(0, UIConstants.CONTROL_HEIGHT));
        spinJourneyDate.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 1; gbc.gridy = 1;
        formCard.add(spinJourneyDate, gbc);

        // Source Station (Read Only)
        gbc.gridx = 1; gbc.gridy = 2;
        formCard.add(createFormLabel("Source Station"), gbc);
        txtSource = createReadOnlyField();
        gbc.gridx = 1; gbc.gridy = 3;
        formCard.add(txtSource, gbc);

        // Destination Station (Read Only)
        gbc.gridx = 1; gbc.gridy = 4;
        formCard.add(createFormLabel("Destination Station"), gbc);
        txtDestination = createReadOnlyField();
        gbc.gridx = 1; gbc.gridy = 5;
        formCard.add(txtDestination, gbc);

        // Departure & Arrival Timings (Read Only)
        JPanel timingsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        timingsPanel.setOpaque(false);
        txtDeparture = createReadOnlyField();
        txtArrival = createReadOnlyField();

        JPanel depSub = new JPanel(new BorderLayout());
        depSub.setOpaque(false);
        depSub.add(createFormLabel("Dep Time"), BorderLayout.NORTH);
        depSub.add(txtDeparture, BorderLayout.SOUTH);

        JPanel arrSub = new JPanel(new BorderLayout());
        arrSub.setOpaque(false);
        arrSub.add(createFormLabel("Arr Time"), BorderLayout.NORTH);
        arrSub.add(txtArrival, BorderLayout.SOUTH);

        timingsPanel.add(depSub);
        timingsPanel.add(arrSub);

        gbc.gridx = 1; gbc.gridy = 6;
        gbc.gridheight = 2; // Span aligned cells
        formCard.add(timingsPanel, gbc);

        add(formCard, BorderLayout.CENTER);

        // Action Buttons Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setOpaque(false);

        btnClear = new JButton("Clear Form");
        btnClear.setPreferredSize(new Dimension(140, UIConstants.CONTROL_HEIGHT));
        btnClear.setFont(UIConstants.FONT_BODY_BOLD);

        btnBook = new JButton("Book Ticket");
        btnBook.setPreferredSize(new Dimension(160, UIConstants.CONTROL_HEIGHT));
        btnBook.putClientProperty(FlatClientProperties.BUTTON_TYPE, "primary");
        btnBook.setBackground(UIConstants.COLOR_PRIMARY);
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(UIConstants.FONT_BODY_BOLD);

        actionPanel.add(btnClear);
        actionPanel.add(btnBook);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_BODY_BOLD);
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }

    private JTextField createInputField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(0, UIConstants.CONTROL_HEIGHT));
        tf.setFont(UIConstants.FONT_BODY);
        tf.setBackground(UIConstants.COLOR_INPUT_BG);
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        return tf;
    }

    private JTextField createReadOnlyField() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setFocusable(false);
        tf.setPreferredSize(new Dimension(0, UIConstants.CONTROL_HEIGHT));
        tf.setFont(UIConstants.FONT_BODY);
        tf.setBackground(UIConstants.COLOR_BG_WINDOW); // Visibly different but high-contrast readable
        tf.setForeground(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        return tf;
    }

    private void setupEvents() {
        cbTrains.addActionListener(e -> {
            Train selected = (Train) cbTrains.getSelectedItem();
            if (selected != null) {
                txtTrainName.setText(selected.getTrainName());
                txtSource.setText(selected.getSourceStation());
                txtDestination.setText(selected.getDestinationStation());
                txtDeparture.setText(selected.getDepartureTime());
                txtArrival.setText(selected.getArrivalTime());
            } else {
                clearTrainFields();
            }
        });

        btnClear.addActionListener(e -> clearForm());
        btnBook.addActionListener(e -> handleBooking());
    }

    public void loadTrains() {
        try {
            cbTrains.removeAllItems();
            List<Train> trains = reservationService.getAllTrains();
            for (Train train : trains) {
                cbTrains.addItem(train);
            }
            if (trains.isEmpty()) {
                cbTrains.addItem(null);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load trains list: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearTrainFields() {
        txtTrainName.setText("");
        txtSource.setText("");
        txtDestination.setText("");
        txtDeparture.setText("");
        txtArrival.setText("");
    }

    private void clearForm() {
        txtPassengerName.setText("");
        cbClassType.setSelectedIndex(0);
        spinJourneyDate.setValue(new Date());
        if (cbTrains.getItemCount() > 0) {
            cbTrains.setSelectedIndex(0);
        }
    }

    private void handleBooking() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "No active user session. Please log in again.",
                    "Session Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String passengerName = txtPassengerName.getText();
        Train selectedTrain = (Train) cbTrains.getSelectedItem();
        String classType = (String) cbClassType.getSelectedItem();
        
        Date spinVal = (Date) spinJourneyDate.getValue();
        LocalDate journeyDate = spinVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (passengerName == null || passengerName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Passenger name cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtPassengerName.requestFocus();
            return;
        }
        if (selectedTrain == null) {
            JOptionPane.showMessageDialog(this, "Please select a train.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            cbTrains.requestFocus();
            return;
        }
        if (journeyDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Journey date cannot be in the past.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            spinJourneyDate.requestFocus();
            return;
        }

        // Beautiful Review Panel inside Dialog
        JPanel reviewPanel = new JPanel(new GridLayout(8, 2, 8, 8));
        reviewPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        reviewPanel.add(new JLabel("Passenger:"));
        JLabel pVal = new JLabel(passengerName); pVal.setFont(UIConstants.FONT_BODY_BOLD);
        reviewPanel.add(pVal);

        reviewPanel.add(new JLabel("Train:"));
        reviewPanel.add(new JLabel(selectedTrain.getTrainNumber() + " - " + selectedTrain.getTrainName()));

        reviewPanel.add(new JLabel("Class Type:"));
        reviewPanel.add(new JLabel(classType));

        reviewPanel.add(new JLabel("Journey Date:"));
        reviewPanel.add(new JLabel(DateUtil.formatLocalDateForDisplay(journeyDate)));

        reviewPanel.add(new JLabel("Route:"));
        reviewPanel.add(new JLabel(selectedTrain.getSourceStation() + "  ->  " + selectedTrain.getDestinationStation()));

        reviewPanel.add(new JLabel("Timings:"));
        reviewPanel.add(new JLabel("Dep: " + selectedTrain.getDepartureTime() + " | Arr: " + selectedTrain.getArrivalTime()));

        reviewPanel.add(new JLabel("Status:"));
        JLabel sVal = new JLabel("CONFIRMED"); sVal.setForeground(UIConstants.COLOR_SUCCESS); sVal.setFont(UIConstants.FONT_BODY_BOLD);
        reviewPanel.add(sVal);

        int option = JOptionPane.showConfirmDialog(this, reviewPanel, 
                "Confirm Booking Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        btnBook.setEnabled(false);
        btnClear.setEnabled(false);

        SwingWorker<Reservation, Void> bookingWorker = new SwingWorker<>() {
            @Override
            protected Reservation doInBackground() throws Exception {
                return reservationService.bookTicket(
                        currentUser.getId(),
                        passengerName,
                        selectedTrain.getTrainNumber(),
                        classType,
                        journeyDate
                );
            }

            @Override
            protected void done() {
                btnBook.setEnabled(true);
                btnClear.setEnabled(true);
                try {
                    Reservation reservation = get();
                    
                    JPanel successPanel = new JPanel(new BorderLayout(10, 15));
                    successPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                    JLabel successTitle = new JLabel("Ticket Booked Successfully!", JLabel.CENTER);
                    successTitle.setFont(UIConstants.FONT_SECTION);
                    successTitle.setForeground(UIConstants.COLOR_SUCCESS);
                    successPanel.add(successTitle, BorderLayout.NORTH);

                    JPanel pnrInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    pnrInfo.setBorder(BorderFactory.createTitledBorder("Unique PNR"));
                    JLabel pnrLabel = new JLabel(reservation.getPnr());
                    pnrLabel.setFont(new Font("Courier New", Font.BOLD, 22));
                    pnrLabel.setForeground(UIConstants.COLOR_PRIMARY);
                    pnrInfo.add(pnrLabel);
                    successPanel.add(pnrInfo, BorderLayout.CENTER);

                    JTextArea descArea = new JTextArea("Passenger: " + reservation.getPassengerName() +
                            "\nTrain: " + selectedTrain.getTrainNumber() + " - " + selectedTrain.getTrainName() +
                            "\nRoute: " + reservation.getSourceStation() + " to " + reservation.getDestinationStation() +
                            "\nClass: " + reservation.getClassType() +
                            "\nDate: " + DateUtil.formatLocalDateForDisplay(reservation.getJourneyDate()) +
                            "\nTimings: Dep: " + selectedTrain.getDepartureTime() + " | Arr: " + selectedTrain.getArrivalTime());
                    descArea.setFont(UIConstants.FONT_BODY);
                    descArea.setEditable(false);
                    descArea.setOpaque(false);
                    successPanel.add(descArea, BorderLayout.SOUTH);

                    Object[] options = {"Book Another", "View My Bookings"};
                    int choice = JOptionPane.showOptionDialog(ReservationPanel.this, successPanel, 
                            "Booking Confirmed", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
                            null, options, options[0]);

                    clearForm();
                    if (choice == 1) {
                        parentFrame.switchPanel("My Bookings");
                    } else {
                        parentFrame.switchPanel("Dashboard");
                    }

                } catch (Exception ex) {
                    Throwable cause = ex.getCause();
                    String msg = cause != null ? cause.getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(ReservationPanel.this, 
                            "Booking failed: " + msg,
                            "Booking Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        bookingWorker.execute();
    }
}
