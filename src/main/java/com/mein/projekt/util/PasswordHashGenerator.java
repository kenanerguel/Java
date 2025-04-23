package com.mein.projekt.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordHashGenerator {
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";

    private static String hashPassword(String name, String pass, String salt) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digester.digest((name + pass + salt).getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // Generate hash for admin user
        String adminHash = hashPassword("admin", "admin123", salt);
        System.out.println("Admin Hash: " + adminHash);

        // Generate hash for scientist user
        String scientistHash = hashPassword("scientist", "scientist123", salt);
        System.out.println("Scientist Hash: " + scientistHash);
    }
} 