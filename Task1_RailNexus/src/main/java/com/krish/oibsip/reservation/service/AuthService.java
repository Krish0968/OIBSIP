package com.krish.oibsip.reservation.service;

import com.krish.oibsip.reservation.dao.UserDAO;
import com.krish.oibsip.reservation.model.User;
import com.krish.oibsip.reservation.util.PasswordUtil;
import com.krish.oibsip.reservation.util.ValidationUtil;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO;
    private User currentUser;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    // Constructor for testing
    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user based on username and password.
     * Sets the currentUser session on success.
     * Throws IllegalArgumentException on invalid input, or SQLException on DB failure.
     */
    public boolean login(String username, String password) throws SQLException {
        // Validate fields not empty
        ValidationUtil.validateLoginCredentials(username, password);

        // Fetch user from DB
        User user = userDAO.getUserByUsername(username.trim());
        if (user == null) {
            return false; // User not found
        }

        // Verify password hash
        if (PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            currentUser = user;
            return true;
        }

        return false; // Password mismatch
    }

    /**
     * Logs out the current user, clearing the session.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Retrieves the currently logged-in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user (useful for setting session directly or mocking).
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Checks if a user session is active.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
