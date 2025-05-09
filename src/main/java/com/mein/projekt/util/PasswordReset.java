package com.mein.projekt.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordReset {
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";

    private static String hashPassword(String username, String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digester.digest((username + password + salt).getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // Neue Passwörter generieren
        String adminNewPass = "admin2024";
        String scienceNewPass = "science2024";

        String adminHash = hashPassword("admin", adminNewPass);
        String scienceHash = hashPassword("science1", scienceNewPass);

        System.out.println("=== Neue Passwörter ===");
        System.out.println("Admin:");
        System.out.println("Username: admin");
        System.out.println("Password: " + adminNewPass);
        System.out.println("Hash: " + adminHash);
        System.out.println("\nScience1:");
        System.out.println("Username: science1");
        System.out.println("Password: " + scienceNewPass);
        System.out.println("Hash: " + scienceHash);
        
        // SQL-Statements zum Aktualisieren der Passwörter
        System.out.println("\n=== SQL-Statements ===");
        System.out.println("UPDATE users SET password = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println("UPDATE users SET password = '" + scienceHash + "' WHERE username = 'science1';");
    }
} 