package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LoginDiagnosticTest {
    
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
        
        System.out.println("Testing login for science1:");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        
        // Run the diagnostic
        LoginDiagnostic.LoginDiagnosticResult result = diagnostic.diagnoseLogin(username, password);
        System.out.println("\nDiagnostic Results:");
        System.out.println("Database Connection: " + (result.isDatabaseConnection() ? "OK" : "FAILED"));
        System.out.println("User Exists: " + (result.isUserExists() ? "YES" : "NO"));
        System.out.println("Password Hashing: " + (result.isPasswordHashing() ? "OK" : "FAILED"));
        System.out.println("Login Attempt: " + (result.isLoginAttempt() ? "SUCCESS" : "FAILED"));
        
        // Generate and compare password hashes
        String generatedHash = CurrentUser.hashPassword(username, password);
        System.out.println("\nPassword Hash Details:");
        System.out.println("Generated Hash: " + generatedHash);
        
        // Try to find user in database
        try {
            UserDAO userDAO = new UserDAO(entityManagerProvider);
            User user = userDAO.isAdminOrClient(username, generatedHash);
            if (user != null) {
                System.out.println("\nUser found in database:");
                System.out.println("ID: " + user.getId());
                System.out.println("Username: " + user.getUsername());
                System.out.println("Is Admin: " + user.isAdmin());
                System.out.println("Stored Hash: " + user.getPassword());
                System.out.println("Hash Match: " + user.getPassword().equals(generatedHash));
            } else {
                System.out.println("\nUser not found or password incorrect");
            }
        } catch (Exception e) {
            System.out.println("\nError accessing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 