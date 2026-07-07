package com.krish.oibsip.reservation;

import com.krish.oibsip.reservation.config.DatabaseInitializer;
import com.krish.oibsip.reservation.model.Reservation;
import com.krish.oibsip.reservation.model.Train;
import com.krish.oibsip.reservation.service.AuthService;
import com.krish.oibsip.reservation.service.ReservationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceLayerTest {

    private static AuthService authService;
    private static ReservationService reservationService;

    @BeforeAll
    public static void setUp() {
        // Initialize/seed the database for tests
        DatabaseInitializer.initializeDatabase();
        authService = new AuthService();
        reservationService = new ReservationService();
    }

    @Test
    public void testAuthenticationFlow() throws Exception {
        // Test successful login
        assertTrue(authService.login("krish", "demo123"));
        assertNotNull(authService.getCurrentUser());
        assertEquals("Krish Sharma", authService.getCurrentUser().getFullName());

        // Test logout
        authService.logout();
        assertNull(authService.getCurrentUser());

        // Test failed login
        assertFalse(authService.login("krish", "wrongpassword"));
        assertFalse(authService.login("nonexistent", "demo123"));
    }

    @Test
    public void testBookingAndCancellationAuthorization() throws Exception {
        // Log in user 1 (krish)
        assertTrue(authService.login("krish", "demo123"));
        int user1Id = authService.getCurrentUser().getId();

        // Book a ticket for user 1
        Reservation ticket = reservationService.bookTicket(
                user1Id,
                "Jane Doe",
                1001, // Nexus Express
                "Sleeper (SL)",
                LocalDate.now().plusDays(2)
        );

        assertNotNull(ticket);
        assertNotNull(ticket.getPnr());
        assertEquals("Jane Doe", ticket.getPassengerName());
        assertEquals("CONFIRMED", ticket.getBookingStatus());

        // Log in user 2 (passenger1)
        assertTrue(authService.login("passenger1", "pass123"));
        int user2Id = authService.getCurrentUser().getId();

        // 1. Authorization: User 2 should NOT be able to view User 1's ticket
        assertThrows(SecurityException.class, () -> {
            reservationService.getReservationByPnr(ticket.getPnr(), user2Id);
        });

        // 2. Authorization: User 2 should NOT be able to cancel User 1's ticket
        assertThrows(SecurityException.class, () -> {
            reservationService.cancelReservation(ticket.getPnr(), user2Id);
        });

        // Log back in as User 1
        assertTrue(authService.login("krish", "demo123"));

        // User 1 should be able to view their own ticket
        Reservation fetched = reservationService.getReservationByPnr(ticket.getPnr(), user1Id);
        assertEquals("Jane Doe", fetched.getPassengerName());

        // Cancel User 1's ticket
        assertDoesNotThrow(() -> {
            reservationService.cancelReservation(ticket.getPnr(), user1Id);
        });

        // Verify status changed to CANCELLED (Soft cancellation)
        Reservation cancelledTicket = reservationService.getReservationByPnr(ticket.getPnr(), user1Id);
        assertEquals("CANCELLED", cancelledTicket.getBookingStatus());
        assertNotNull(cancelledTicket.getCancelledAt());

        // 3. Duplicate Cancellation: Cannot cancel an already cancelled ticket
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.cancelReservation(ticket.getPnr(), user1Id);
        });
    }

    @Test
    public void testBookingInvalidTrain() {
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.bookTicket(
                    1,
                    "John Smith",
                    9999, // Invalid train number
                    "Sleeper (SL)",
                    LocalDate.now()
            );
        });
    }
}
