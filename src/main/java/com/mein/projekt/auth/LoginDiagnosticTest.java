package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LoginDiagnosticTest {
    
    @Inject
    private LoginDiagnostic loginDiagnostic;
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: LoginDiagnosticTest <username> <password>");
            return;
        }
        
        String username = args[0];
        String password = args[1];
        
        // Initialize the required components
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        entityManagerProvider.init();
        
        LoginDiagnostic diagnostic = new LoginDiagnostic();
        diagnostic.entityManagerProvider = entityManagerProvider;
        diagnostic.userDAO = new UserDAO(entityManagerProvider);
        
        // Run the diagnostic
        LoginDiagnostic.LoginDiagnosticResult result = diagnostic.diagnoseLogin(username, password);
        System.out.println(result.toString());
    }
} 