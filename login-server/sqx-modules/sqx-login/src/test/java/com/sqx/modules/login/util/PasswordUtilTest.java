package com.sqx.modules.login.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void encrypt_shouldReturnMd5Hex() {
        assertEquals("0192023a7bbd73250516f069df18b500", PasswordUtil.encrypt("admin123"));
        assertEquals("6ad14ba9986e3615423dfca256d04e3f", PasswordUtil.encrypt("user123"));
    }

    @Test
    void matches_shouldValidatePlainAndEncryptedPassword() {
        String encrypted = PasswordUtil.encrypt("secret");
        assertTrue(PasswordUtil.matches("secret", encrypted));
        assertFalse(PasswordUtil.matches("wrong", encrypted));
    }
}
