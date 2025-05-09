package com.mein.projekt.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordHashTest {
    public static void main(String[] args) {
        String password = "scientist123";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String hashedPassword = Base64.getEncoder().encodeToString(hash);
            System.out.println("Password: " + password);
            System.out.println("Hashed: " + hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 