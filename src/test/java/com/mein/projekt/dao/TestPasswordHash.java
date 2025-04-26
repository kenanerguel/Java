package com.mein.projekt.dao;

import com.mein.projekt.util.EntityManagerProvider;

public class TestPasswordHash {
    public static void main(String[] args) {
        // Test für Admin-Benutzer
        String adminUsername = "admin";
        String adminPassword = "admin123"; // Standard-Passwort für Admin
        
        // Test für Wissenschaftler
        String scientistUsername = "science1";
        String scientistPassword = "science123"; // Standard-Passwort für Wissenschaftler
        
        UserDAO userDAO = new UserDAO(new EntityManagerProvider());
        
        System.out.println("Test Hash für Admin:");
        System.out.println("Username: " + adminUsername);
        System.out.println("Password: " + adminPassword);
        System.out.println("Generated Hash: " + userDAO.hashPassword(adminUsername, adminPassword));
        System.out.println("\nStored Hash from DB:");
        System.out.println("pRnf+TXORKJefRH6x9HcfOyIxKEetJvxAqZc10GgB/bxuzFtjHMtSLqaPMYAU9zelkfUwAaP8S0QnclBkbgVJQ==");
        
        System.out.println("\nTest Hash für Wissenschaftler:");
        System.out.println("Username: " + scientistUsername);
        System.out.println("Password: " + scientistPassword);
        System.out.println("Generated Hash: " + userDAO.hashPassword(scientistUsername, scientistPassword));
        System.out.println("\nStored Hash from DB:");
        System.out.println("KW/Q+quBB936f+vEzM79JDs+4TYDraef7VS8i/vAS8fj6Zr+fvwOIk28l1G7IP0p1JmEeNvJj+BBdFia6EXKUw==");
    }
} 