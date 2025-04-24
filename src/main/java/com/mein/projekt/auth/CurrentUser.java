package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.logging.Level;

@Named
@SessionScoped
public class CurrentUser implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CurrentUser.class.getName());
    
    @Inject
    private EntityManager entityManager;
    
    @Inject
    private UserDAO userDAO;
    
    private User user = null;
    
    public CurrentUser() {
        LOGGER.finest("CurrentUser wurde erstellt");
    }

    public void handleUser(String username, String password) {
        try {
            System.out.println("=== Login-Versuch in CurrentUser ===");
            System.out.println("Benutzername: " + username);
            System.out.println("Rohes Passwort (Länge): " + password.length());
            
            String passHash = hashPassword(password);
            System.out.println("Generierter Hash: " + passHash);
            System.out.println("Hash-Länge: " + passHash.length());
            
            user = userDAO.isAdminOrClient(username, passHash);
            
            if (user != null) {
                System.out.println("Login erfolgreich für: " + username);
            } else {
                System.out.println("Login fehlgeschlagen für: " + username);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Login", e);
            user = null;
        }
    }

    public boolean isValid() {
        return user != null;
    }

    public void reset() {
        this.user = null;
    }

    private static String hashPassword(String password) {
        try {
            // Using SHA-256 for password hashing
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Convert the password string to bytes using UTF-8 encoding
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            // Convert the byte array to a Base64 string
            String result = Base64.getEncoder().encodeToString(hash);
            System.out.println("Password hashing details:");
            System.out.println("Input password length: " + password.length());
            System.out.println("Generated hash: " + result);
            System.out.println("Hash length: " + result.length());
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Hashen des Passworts", e);
            throw new RuntimeException(e);
        }
    }

    public User getUser() {
        return this.user;
    }
}
