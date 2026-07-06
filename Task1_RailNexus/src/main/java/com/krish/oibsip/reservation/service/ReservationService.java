package com.krish.oibsip.reservation.service;

import com.krish.oibsip.reservation.dao.ReservationDAO;
import com.krish.oibsip.reservation.dao.TrainDAO;
import com.krish.oibsip.reservation.model.Reservation;
import com.krish.oibsip.reservation.model.Train;
import com.krish.oibsip.reservation.util.PNRGenerator;
import com.krish.oibsip.reservation.util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final TrainDAO trainDAO;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.trainDAO = new TrainDAO();
    }

    // Constructor for testing
    public ReservationService(ReservationDAO reservationDAO, TrainDAO trainDAO) {
        this.reservationDAO = reservationDAO;
        this.trainDAO = trainDAO;
    }

    /**
     * Retrieves all trains for combobox listing.
     */
    public List<Train> getAllTrains() throws SQLException {
        return trainDAO.getAllTrains();
    }

    /**
     * Resolves a train by its number.
     */
    public Train getTrainByNumber(int trainNumber) throws SQLException {
        return trainDAO.getTrainByNumber(trainNumber);
    }

    /**
     * Book a new ticket.
     * Generates a unique PNR, performs business validations, and inserts the record.
     */
    public Reservation bookTicket(int userId, String passengerName, int trainNumber, String classType, 
                                  LocalDate journeyDate) throws SQLException {
        // 1. Basic Form Validations
        ValidationUtil.validatePassengerName(passengerName);
        ValidationUtil.validateJourneyDate(journeyDate);
        if (classType == null || classType.trim().isEmpty()) {
            throw new IllegalArgumentException("Class type must be selected.");
        }

        // 2. Fetch Train details and validate it exists
        Train train = trainDAO.getTrainByNumber(trainNumber);
        if (train == null) {
            throw new IllegalArgumentException("Train number " + trainNumber + " does not exist.");
        }

        // 3. Station validation (using details from train)
        ValidationUtil.validateStations(train.getSourceStation(), train.getDestinationStation());

        // 4. Generate Unique PNR using database existence checker callback
        String uniquePnr = PNRGenerator.generateUniquePNR(pnr -> {
            try {
                return reservationDAO.isPnrExists(pnr);
            } catch (SQLException e) {
                // If DB check fails, treat as exists to retry
                return true;
            }
        });

        // 5. Build Reservation object
        Reservation reservation = new Reservation();
        reservation.setPnr(uniquePnr);
        reservation.setUserId(userId);
        reservation.setPassengerName(passengerName.trim());
        reservation.setTrainId(train.getId());
        reservation.setClassType(classType);
        reservation.setJourneyDate(journeyDate);
        reservation.setSourceStation(train.getSourceStation());
        reservation.setDestinationStation(train.getDestinationStation());
        reservation.setBookingStatus("CONFIRMED");
        reservation.setBookedAt(LocalDateTime.now());
        
        // Transient fields for return info
        reservation.setTrainNumber(train.getTrainNumber());
        reservation.setTrainName(train.getTrainName());

        // 6. Insert in DB
        boolean success = reservationDAO.insertReservation(reservation);
        if (!success) {
            throw new SQLException("Failed to save reservation in the database.");
        }

        return reservation;
    }

    /**
     * Retrieves all bookings made by a particular user.
     */
    public List<Reservation> getBookingsForUser(int userId) throws SQLException {
        return reservationDAO.getReservationsByUserId(userId);
    }

    /**
     * Looks up reservation details by PNR.
     * Validates that the PNR exists and belongs to the specified user.
     */
    public Reservation getReservationByPnr(String pnr, int currentUserId) throws SQLException {
        ValidationUtil.validatePNR(pnr);
        
        Reservation reservation = reservationDAO.getReservationByPnr(pnr);
        if (reservation == null) {
            throw new IllegalArgumentException("No reservation found for PNR: " + pnr.toUpperCase());
        }

        // Authorization check: prevent user from fetching another user's reservation
        if (reservation.getUserId() != currentUserId) {
            throw new SecurityException("Access Denied: You are not authorized to view this booking.");
        }

        return reservation;
    }

    /**
     * Cancels an existing reservation.
     * Verifies PNR, status, and ownership prior to executing cancellation.
     */
    public void cancelReservation(String pnr, int currentUserId) throws SQLException {
        // 1. Fetch reservation and verify existence + ownership
        Reservation reservation = getReservationByPnr(pnr, currentUserId);

        // 2. Prevent duplicate cancellation
        if ("CANCELLED".equalsIgnoreCase(reservation.getBookingStatus())) {
            throw new IllegalArgumentException("Reservation with PNR " + pnr.toUpperCase() + " has already been cancelled.");
        }

        // 3. Perform cancellation in DB
        boolean success = reservationDAO.updateReservationStatus(pnr, "CANCELLED", LocalDateTime.now());
        if (!success) {
            throw new SQLException("Failed to update reservation cancellation status in the database.");
        }
    }
}
