package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import java.util.logging.Logger;

public class LoginTest {
    private static final Logger LOGGER = Logger.getLogger(LoginTest.class.getName());

    public static void main(String[] args) {
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        UserDAO userDAO = new UserDAO(entityManagerProvider);
        CurrentUser currentUser = new CurrentUser();
        currentUser.init(entityManagerProvider);

        // Test admin login
        System.out.println("Testing admin login:");
        testLogin(currentUser, "admin", "admin123");

        // Test science1 login
        System.out.println("\nTesting science1 login:");
        testLogin(currentUser, "science1", "pass123");

        // Test invalid login
        System.out.println("\nTesting invalid login:");
        testLogin(currentUser, "invalid", "invalid");
    }

    private static void testLogin(CurrentUser currentUser, String username, String password) {
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        
        // Generate hash
        String hash = CurrentUser.hashPassword(username, password);
        System.out.println("Generated hash: " + hash);
        
        // Try login
        currentUser.handleUser(username, password);
        
        if (currentUser.isValid()) {
            User user = currentUser.getUser();
            System.out.println("Login successful!");
            System.out.println("User details:");
            System.out.println("- ID: " + user.getId());
            System.out.println("- Username: " + user.getUsername());
            System.out.println("- Is Admin: " + user.isAdmin());
        } else {
            System.out.println("Login failed!");
        }
        
        // Reset for next test
        currentUser.reset();
    }
} 