package com.mein.projekt.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordGenerator {
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";
    
    public static void main(String[] args) {
        // Admin user
        String adminUsername = "admin";
        String adminPassword = "admin123";
        String adminHash = hashPassword(adminUsername, adminPassword);
        
        // Scientist user
        String scientistUsername = "science1";
        String scientistPassword = "science123";
        String scientistHash = hashPassword(scientistUsername, scientistPassword);
        
        System.out.println("Admin Login Details:");
        System.out.println("Username: " + adminUsername);
        System.out.println("Password: " + adminPassword);
        System.out.println("Hash: " + adminHash);
        System.out.println("\nScientist Login Details:");
        System.out.println("Username: " + scientistUsername);
        System.out.println("Password: " + scientistPassword);
        System.out.println("Hash: " + scientistHash);
    }
    
    private static String hashPassword(String username, String password) {
        try {
            String saltedPassword = username + salt + password;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
} 