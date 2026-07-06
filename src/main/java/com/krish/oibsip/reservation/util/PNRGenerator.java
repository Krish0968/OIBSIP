package com.krish.oibsip.reservation.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class PNRGenerator {
    private static final String PREFIX = "RNX";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Generates a candidate PNR string: RNX-YYYYMMDD-SUFFIX
     */
    public static String generatePNR() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        StringBuilder suffix = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            suffix.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return PREFIX + "-" + datePart + "-" + suffix.toString();
    }

    /**
     * Generates a unique PNR. Checks against the provided Predicate (which queries the DB)
     * and retries in case of collisions.
     */
    public static String generateUniquePNR(Predicate<String> pnrExistsChecker) {
        String pnr;
        int attempts = 0;
        do {
            pnr = generatePNR();
            attempts++;
            if (attempts > 10) {
                throw new RuntimeException("Failed to generate a unique PNR after 10 attempts.");
            }
        } while (pnrExistsChecker.test(pnr));
        return pnr;
    }
}
