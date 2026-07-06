package com.krish.oibsip.reservation;

import com.krish.oibsip.reservation.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testHashAndPasswordMatching() {
        String plain = "demo123";
        String hash = PasswordUtil.hashPassword(plain);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$")); // Standard BCrypt prefix
        assertTrue(PasswordUtil.checkPassword(plain, hash));
    }

    @Test
    public void testPasswordMismatch() {
        String plain = "demo123";
        String hash = PasswordUtil.hashPassword(plain);

        assertFalse(PasswordUtil.checkPassword("wrongpass", hash));
        assertFalse(PasswordUtil.checkPassword(null, hash));
        assertFalse(PasswordUtil.checkPassword(plain, null));
        assertFalse(PasswordUtil.checkPassword(plain, "invalidbcryptstructure"));
    }
}
