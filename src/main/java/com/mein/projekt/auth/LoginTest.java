package com.mein.projekt.auth;

import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class LoginTest {
    public static void main(String[] args) {
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        CurrentUser currentUser = new CurrentUser();
        currentUser.init(entityManagerProvider);

        // Test admin login
        System.out.println("Testing admin login:");
        testLogin(currentUser, "admin", "admin123");

        // Test science1 login specifically
        System.out.println("\nTesting science1 login in detail:");
        String username = "science1";
        String password = "pass123";
        
        // Generate hash
        String hash = CurrentUser.hashPassword(username, password);
        System.out.println("Generated hash for science1: " + hash);
        
        // Try to find user in database
        try {
            EntityManager em = entityManagerProvider.getEntityManager();
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            User user = query.getSingleResult();
            System.out.println("User found in database:");
            System.out.println("- Username: " + user.getUsername());
            System.out.println("- Stored hash: " + user.getPassword());
            System.out.println("- Generated hash matches stored hash: " + user.getPassword().equals(hash));
        } catch (Exception e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        
        // Test login
        testLogin(currentUser, username, password);

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