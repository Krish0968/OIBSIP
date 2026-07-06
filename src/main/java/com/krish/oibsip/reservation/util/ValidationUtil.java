package com.krish.oibsip.reservation.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern PNR_PATTERN = Pattern.compile("^RNX-\\d{8}-[A-Z0-9]{6}$");

    /**
     * Validates that passenger name is not null or empty (after trimming).
     */
    public static void validatePassengerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name cannot be empty.");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Passenger name must be at least 2 characters long.");
        }
    }

    /**
     * Validates that train number is a valid positive integer.
     */
    public static int validateTrainNumber(String trainNumberStr) {
        if (trainNumberStr == null || trainNumberStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Train number cannot be empty.");
        }
        try {
            int trainNumber = Integer.parseInt(trainNumberStr.trim());
            if (trainNumber <= 0) {
                throw new IllegalArgumentException("Train number must be a positive integer.");
            }
            return trainNumber;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Train number must contain only digits.");
        }
    }

    /**
     * Validates that the journey date is not null, not in the past.
     */
    public static void validateJourneyDate(LocalDate journeyDate) {
        if (journeyDate == null) {
            throw new IllegalArgumentException("Journey date is required.");
        }
        if (journeyDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Journey date cannot be in the past.");
        }
    }

    /**
     * Validates that source and destination are not identical and are not empty.
     */
    public static void validateStations(String source, String destination) {
        if (source == null || source.trim().isEmpty()) {
            throw new IllegalArgumentException("Source station is required.");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination station is required.");
        }
        if (source.trim().equalsIgnoreCase(destination.trim())) {
            throw new IllegalArgumentException("Source and destination stations cannot be the same.");
        }
    }

    /**
     * Validates PNR format.
     */
    public static void validatePNR(String pnr) {
        if (pnr == null || pnr.trim().isEmpty()) {
            throw new IllegalArgumentException("PNR cannot be empty.");
        }
        if (!PNR_PATTERN.matcher(pnr.trim().toUpperCase()).matches()) {
            throw new IllegalArgumentException("Invalid PNR format. Expected format: RNX-YYYYMMDD-XXXXXX");
        }
    }

    /**
     * Validates that username and password are not empty.
     */
    public static void validateLoginCredentials(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }
}
