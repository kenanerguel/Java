package com.mein.projekt.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordGenerator {
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";

    public static String generateHash(String username, String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            // Correct order: username + password + salt
            byte[] hashBytes = digester.digest((username + password + salt).getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 