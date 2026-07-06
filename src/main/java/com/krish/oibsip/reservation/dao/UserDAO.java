package com.krish.oibsip.reservation.dao;

import com.krish.oibsip.reservation.config.DatabaseConnection;
import com.krish.oibsip.reservation.model.User;
import com.krish.oibsip.reservation.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Finds a User in the database by their unique username.
     * Returns null if no user is found.
     */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, full_name, created_at FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setFullName(rs.getString("full_name"));
                    user.setCreatedAt(DateUtil.parseLocalDateTimeFromDB(rs.getString("created_at")));
                    return user;
                }
            }
        }
        return null;
    }
}
