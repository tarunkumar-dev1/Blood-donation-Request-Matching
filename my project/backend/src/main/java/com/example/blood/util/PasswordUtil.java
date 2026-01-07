package com.example.blood.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt.
 * Provides secure password storage and authentication.
 */
public final class PasswordUtil {
    
    private PasswordUtil() {}
    
    /**
     * Hash a plain text password using BCrypt with cost of 12.
     * @param plainPassword The plain text password to hash
     * @return The hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }
    
    /**
     * Verify a plain text password against a hashed password.
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The hashed password to compare against
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}
