package com.krish.oibsip.reservation;

import com.formdev.flatlaf.FlatDarkLaf;
import com.krish.oibsip.reservation.config.DatabaseInitializer;
import com.krish.oibsip.reservation.service.AuthService;
import com.krish.oibsip.reservation.ui.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // 1. Initialize Database Tables and Seeds
        try {
            DatabaseInitializer.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Fatal: Database initialization failed. Shutting down.");
            e.printStackTrace();
            System.exit(1);
        }

        // 2. Launch GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Configure FlatLaf Dark Theme
                FlatDarkLaf.setup();
            } catch (Exception e) {
                System.err.println("Warning: Failed to load FlatLaf Look and Feel. " + e.getMessage());
            }

            AuthService authService = new AuthService();
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);
        });
    }
}
