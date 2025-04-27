package com.mein.projekt.dao;

import com.mein.projekt.util.EntityManagerProvider;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestPasswordHash {
    
    @Test
    public void testPasswordHashing() {
        // Test für Admin-Benutzer
        String adminUsername = "admin";
        String adminPassword = "admin123"; // Standard-Passwort für Admin
        
        // Test für Wissenschaftler
        String scientistUsername = "science1";
        String scientistPassword = "science123"; // Standard-Passwort für Wissenschaftler
        
        UserDAO userDAO = new UserDAO(new EntityManagerProvider());
        
        // Test für Admin
        String adminGeneratedHash = userDAO.hashPassword(adminUsername, adminPassword);
        String adminStoredHash = "pRnf+TXORKJefRH6x9HcfOyIxKEetJvxAqZc10GgB/bxuzFtjHMtSLqaPMYAU9zelkfUwAaP8S0QnclBkbgVJQ==";
        
        System.out.println("Test Hash für Admin:");
        System.out.println("Username: " + adminUsername);
        System.out.println("Password: " + adminPassword);
        System.out.println("Generated Hash: " + adminGeneratedHash);
        System.out.println("Stored Hash:    " + adminStoredHash);
        
        assertEquals(adminStoredHash, adminGeneratedHash, "Admin-Hash stimmt nicht überein");
        
        // Test für Wissenschaftler
        String scientistGeneratedHash = userDAO.hashPassword(scientistUsername, scientistPassword);
        String scientistStoredHash = userDAO.hashPassword(scientistUsername, scientistPassword);
        
        System.out.println("\nTest Hash für Wissenschaftler:");
        System.out.println("Username: " + scientistUsername);
        System.out.println("Password: " + scientistPassword);
        System.out.println("Generated Hash: " + scientistGeneratedHash);
        System.out.println("Stored Hash:    " + scientistStoredHash);
        
        assertEquals(scientistStoredHash, scientistGeneratedHash, "Wissenschaftler-Hash stimmt nicht überein");
    }
} 