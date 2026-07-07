package com.krish.oibsip.reservation;

import com.krish.oibsip.reservation.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    public void testPassengerNameValidation() {
        // Valid name
        assertDoesNotThrow(() -> ValidationUtil.validatePassengerName("Krish Sharma"));

        // Invalid name (null, empty, too short)
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validatePassengerName(null));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validatePassengerName(""));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validatePassengerName("   "));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validatePassengerName("A"));
    }

    @Test
    public void testTrainNumberValidation() {
        // Valid train number
        assertEquals(1001, ValidationUtil.validateTrainNumber("1001"));

        // Invalid train numbers
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateTrainNumber(null));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateTrainNumber(""));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateTrainNumber("abc"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateTrainNumber("-101"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateTrainNumber("0"));
    }

    @Test
    public void testJourneyDateValidation() {
        // Valid dates (Today and Future)
        assertDoesNotThrow(() -> ValidationUtil.validateJourneyDate(LocalDate.now()));
        assertDoesNotThrow(() -> ValidationUtil.validateJourneyDate(LocalDate.now().plusDays(5)));

        // Invalid dates (Past date, null)
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateJourneyDate(null));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateJourneyDate(LocalDate.now().minusDays(1)));
    }

    @Test
    public void testStationValidation() {
        // Valid stations
        assertDoesNotThrow(() -> ValidationUtil.validateStations("New Delhi", "Mumbai Central"));

        // Invalid stations (identical, empty)
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateStations("New Delhi", "New Delhi"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateStations("New Delhi", "new delhi"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateStations("", "Mumbai"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateStations("Delhi", null));
    }

    @Test
    public void testPNRValidation() {
        // Valid PNR formats
        assertDoesNotThrow(() -> ValidationUtil.validatePNR("RNX-20260706-A7K9Q2"));

        // Invalid PNR formats
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validatePNR(""));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validatePNR("RNX-123-ABC"));
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validatePNR("rnx-20260706-a7k9q")); // 5 chars suffix instead of 6
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validatePNR("PNR-20260706-A7K9Q2")); // Wrong prefix
    }
}
