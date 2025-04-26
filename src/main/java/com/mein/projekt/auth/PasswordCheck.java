package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class PasswordCheck {
    public static void main(String[] args) {
        // Initialize the required components
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        entityManagerProvider.init();
        
        EntityManager em = entityManagerProvider.getEntityManager();
        
        // Get the stored password hash for science1
        TypedQuery<String> query = em.createQuery(
            "SELECT u.password FROM User u WHERE u.username = :username", String.class);
        query.setParameter("username", "science1");
        
        String storedHash = query.getSingleResult();
        System.out.println("Stored password hash for science1: " + storedHash);
        
        // Generate a new hash with the test password
        String testHash = CurrentUser.hashPassword("science1", "pass123");
        System.out.println("Generated hash for 'pass123': " + testHash);
        
        // Compare the hashes
        System.out.println("Hashes match: " + storedHash.equals(testHash));
    }
} 