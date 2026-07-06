package com.krish.oibsip.reservation.dao;

import com.krish.oibsip.reservation.config.DatabaseConnection;
import com.krish.oibsip.reservation.model.Train;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO {

    /**
     * Retrieves all trains from the database sorted by train number.
     */
    public List<Train> getAllTrains() throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT id, train_number, train_name, source_station, destination_station, departure_time, arrival_time FROM trains ORDER BY train_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Train train = new Train(
                        rs.getInt("id"),
                        rs.getInt("train_number"),
                        rs.getString("train_name"),
                        rs.getString("source_station"),
                        rs.getString("destination_station"),
                        rs.getString("departure_time"),
                        rs.getString("arrival_time")
                );
                trains.add(train);
            }
        }
        return trains;
    }

    /**
     * Retrieves a train by its unique train number.
     */
    public Train getTrainByNumber(int trainNumber) throws SQLException {
        String sql = "SELECT id, train_number, train_name, source_station, destination_station, departure_time, arrival_time FROM trains WHERE train_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, trainNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Train(
                            rs.getInt("id"),
                            rs.getInt("train_number"),
                            rs.getString("train_name"),
                            rs.getString("source_station"),
                            rs.getString("destination_station"),
                            rs.getString("departure_time"),
                            rs.getString("arrival_time")
                    );
                }
            }
        }
        return null;
    }
}
