package com.krish.oibsip.reservation;

import com.krish.oibsip.reservation.util.PNRGenerator;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class PNRGeneratorTest {

    @Test
    public void testPNRFormat() {
        String pnr = PNRGenerator.generatePNR();
        assertNotNull(pnr);
        // Format should match RNX-YYYYMMDD-XXXXXX (total length 3 + 1 + 8 + 1 + 6 = 19 characters)
        assertEquals(19, pnr.length());
        assertTrue(pnr.startsWith("RNX-"));
        assertTrue(pnr.matches("^RNX-\\d{8}-[A-Z0-9]{6}$"));
    }

    @Test
    public void testUniquePNRGenerationRetry() {
        AtomicInteger checkCount = new AtomicInteger(0);

        // Simulated check: claim the first generated PNR already exists, but the second one is unique
        String uniquePnr = PNRGenerator.generateUniquePNR(pnr -> {
            int count = checkCount.incrementAndGet();
            return count < 2; // Returns true (exists) on first try, false (does not exist) on second
        });

        assertNotNull(uniquePnr);
        assertEquals(2, checkCount.get(), "Should have checked twice due to simulated collision.");
    }
}
