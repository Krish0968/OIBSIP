package com.krish.oibsip.reservation.dao;

import com.krish.oibsip.reservation.config.DatabaseConnection;
import com.krish.oibsip.reservation.model.Reservation;
import com.krish.oibsip.reservation.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    /**
     * Checks if a PNR already exists in the database.
     */
    public boolean isPnrExists(String pnr) throws SQLException {
        String sql = "SELECT 1 FROM reservations WHERE pnr = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Inserts a new reservation record in the database.
     */
    public boolean insertReservation(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservations (pnr, user_id, passenger_name, train_id, class_type, " +
                "journey_date, source_station, destination_station, booking_status, booked_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, reservation.getPnr());
            ps.setInt(2, reservation.getUserId());
            ps.setString(3, reservation.getPassengerName());
            ps.setInt(4, reservation.getTrainId());
            ps.setString(5, reservation.getClassType());
            ps.setString(6, DateUtil.formatLocalDateForDB(reservation.getJourneyDate()));
            ps.setString(7, reservation.getSourceStation());
            ps.setString(8, reservation.getDestinationStation());
            ps.setString(9, reservation.getBookingStatus());
            ps.setString(10, DateUtil.formatLocalDateTimeForDB(reservation.getBookedAt()));
            
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * Retrieves all reservations booked by a specific user.
     * Joins with the trains table to auto-fill train name and number.
     */
    public List<Reservation> getReservationsByUserId(int userId) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.id, r.pnr, r.user_id, r.passenger_name, r.train_id, r.class_type, " +
                "r.journey_date, r.source_station, r.destination_station, r.booking_status, " +
                "r.booked_at, r.cancelled_at, t.train_number, t.train_name " +
                "FROM reservations r " +
                "JOIN trains t ON r.train_id = t.id " +
                "WHERE r.user_id = ? " +
                "ORDER BY r.booked_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation res = mapRowToReservation(rs);
                    list.add(res);
                }
            }
        }
        return list;
    }

    /**
     * Retrieves reservation details by PNR.
     * Joins with the trains table to auto-fill train name and number.
     */
    public Reservation getReservationByPnr(String pnr) throws SQLException {
        String sql = "SELECT r.id, r.pnr, r.user_id, r.passenger_name, r.train_id, r.class_type, " +
                "r.journey_date, r.source_station, r.destination_station, r.booking_status, " +
                "r.booked_at, r.cancelled_at, t.train_number, t.train_name " +
                "FROM reservations r " +
                "JOIN trains t ON r.train_id = t.id " +
                "WHERE r.pnr = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, pnr.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReservation(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates reservation status (CONFIRMED / CANCELLED) and cancelled_at timestamp.
     */
    public boolean updateReservationStatus(String pnr, String status, LocalDateTime cancelledAt) throws SQLException {
        String sql = "UPDATE reservations SET booking_status = ?, cancelled_at = ? WHERE pnr = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setString(2, cancelledAt != null ? DateUtil.formatLocalDateTimeForDB(cancelledAt) : null);
            ps.setString(3, pnr.trim().toUpperCase());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        Reservation res = new Reservation(
                rs.getInt("id"),
                rs.getString("pnr"),
                rs.getInt("user_id"),
                rs.getString("passenger_name"),
                rs.getInt("train_id"),
                rs.getString("class_type"),
                DateUtil.parseLocalDateFromDB(rs.getString("journey_date")),
                rs.getString("source_station"),
                rs.getString("destination_station"),
                rs.getString("booking_status"),
                DateUtil.parseLocalDateTimeFromDB(rs.getString("booked_at")),
                DateUtil.parseLocalDateTimeFromDB(rs.getString("cancelled_at"))
        );
        res.setTrainNumber(rs.getInt("train_number"));
        res.setTrainName(rs.getString("train_name"));
        return res;
    }
}
