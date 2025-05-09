package com.mein.projekt.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class FixPasswordHashes {
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";

    public static String hashPassword(String username, String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            // Correct order: username + password + salt
            byte[] hashBytes = digester.digest((username + password + salt).getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // Generate hashes for both users
        String adminHash = hashPassword("admin", "admin123");
        String scientistHash = hashPassword("science1", "science123");

        System.out.println("=== Generated Password Hashes ===");
        System.out.println("\nAdmin User:");
        System.out.println("Username: admin");
        System.out.println("Password: admin123");
        System.out.println("Hash: " + adminHash);
        
        System.out.println("\nScientist User:");
        System.out.println("Username: science1");
        System.out.println("Password: science123");
        System.out.println("Hash: " + scientistHash);
        
        System.out.println("\n=== SQL Update Statements ===");
        System.out.println("UPDATE users SET password = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println("UPDATE users SET password = '" + scientistHash + "' WHERE username = 'science1';");
    }
} 