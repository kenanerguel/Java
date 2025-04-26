package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class LoginDiagnosticTest {
    private static final Logger LOGGER = Logger.getLogger(LoginDiagnosticTest.class.getName());
    
    @Inject
    private LoginDiagnostic loginDiagnostic;
    
    public static void main(String[] args) {
        // Initialize the required components
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        entityManagerProvider.init();
        
        LoginDiagnostic diagnostic = new LoginDiagnostic();
        diagnostic.entityManagerProvider = entityManagerProvider;
        diagnostic.userDAO = new UserDAO(entityManagerProvider);
        
        // Test science1 login
        String username = "science1";
        String password = "pass123";
        
        System.out.println("\n=== Detailed Login Diagnostic for science1 ===");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        
        // 1. Check database connection
        System.out.println("\n1. Checking database connection...");
        EntityManager em = entityManagerProvider.getEntityManager();
        if (em == null) {
            System.out.println("ERROR: EntityManager is null");
            return;
        }
        try {
            em.createNativeQuery("SELECT 1").getSingleResult();
            System.out.println("Database connection: OK");
        } catch (Exception e) {
            System.out.println("ERROR: Database connection failed: " + e.getMessage());
            return;
        }
        
        // 1.5 Create user if it doesn't exist
        System.out.println("\n1.5. Checking if user needs to be created...");
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            try {
                query.getSingleResult();
                System.out.println("User already exists, skipping creation");
            } catch (Exception e) {
                System.out.println("User does not exist, creating new user...");
                User newUser = new User();
                newUser.setUsername(username);
                // Set the exact hash that we know works
                newUser.setPassword("KW/Q+quBB936f+vEzM79JDs+4TYDraef7VS8i/vAS8fj6Zr+fvwOIk28l1G7IP0p1JmEeNvJj+BBdFia6EXKUw==");
                newUser.setAdmin(false);
                em.getTransaction().begin();
                em.persist(newUser);
                em.getTransaction().commit();
                System.out.println("User created successfully with correct hash");
            }
        } catch (Exception e) {
            System.out.println("ERROR: Failed to check/create user: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // 2. Check if user exists
        System.out.println("\n2. Checking if user exists...");
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            User user = query.getSingleResult();
            System.out.println("User found in database:");
            System.out.println("- ID: " + user.getId());
            System.out.println("- Username: " + user.getUsername());
            System.out.println("- Is Admin: " + user.isAdmin());
            System.out.println("- Stored Hash: " + user.getPassword());
        } catch (Exception e) {
            System.out.println("ERROR: User not found or error accessing database: " + e.getMessage());
            return;
        }
        
        // 3. Check password hashing
        System.out.println("\n3. Checking password hashing...");
        String generatedHash = CurrentUser.hashPassword(username, password);
        System.out.println("Generated hash: " + generatedHash);
        
        // 4. Try to authenticate
        System.out.println("\n4. Attempting authentication...");
        try {
            User authenticatedUser = diagnostic.userDAO.isAdminOrClient(username, generatedHash);
            if (authenticatedUser != null) {
                System.out.println("Authentication successful!");
                System.out.println("User details:");
                System.out.println("- ID: " + authenticatedUser.getId());
                System.out.println("- Username: " + authenticatedUser.getUsername());
                System.out.println("- Is Admin: " + authenticatedUser.isAdmin());
                System.out.println("- Stored Hash: " + authenticatedUser.getPassword());
                System.out.println("- Hash Match: " + authenticatedUser.getPassword().equals(generatedHash));
            } else {
                System.out.println("ERROR: Authentication failed - user not found or password incorrect");
            }
        } catch (Exception e) {
            System.out.println("ERROR: Authentication failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 5. Check CurrentUser handling
        System.out.println("\n5. Testing CurrentUser handling...");
        try {
            CurrentUser currentUser = new CurrentUser();
            currentUser.init(entityManagerProvider);
            currentUser.handleUser(username, password);
            if (currentUser.isValid()) {
                System.out.println("CurrentUser authentication successful!");
                System.out.println("User details:");
                System.out.println("- Username: " + currentUser.getUser().getUsername());
                System.out.println("- Is Admin: " + currentUser.getUser().isAdmin());
            } else {
                System.out.println("ERROR: CurrentUser authentication failed");
            }
        } catch (Exception e) {
            System.out.println("ERROR: CurrentUser handling failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 