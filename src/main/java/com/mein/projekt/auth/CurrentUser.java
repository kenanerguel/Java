package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
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
    private EntityManagerProvider entityManagerProvider;
    
    private UserDAO userDAO;
    private User user = null;
    
    public CurrentUser() {
        LOGGER.info("CurrentUser wurde erstellt");
    }
    
    @Inject
    public void init(EntityManagerProvider entityManagerProvider) {
        try {
            LOGGER.info("Initialisiere UserDAO");
            this.userDAO = new UserDAO();
            this.userDAO.setEntityManagerProvider(entityManagerProvider);
            LOGGER.info("UserDAO erfolgreich initialisiert");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Initialisieren des UserDAO", e);
        }
    }

    public void handleUser(String username, String password) {
        try {
            if (userDAO == null) {
                LOGGER.severe("UserDAO ist null. Initialisiere es jetzt...");
                this.userDAO = new UserDAO();
                this.userDAO.setEntityManagerProvider(entityManagerProvider);
            }
            
            LOGGER.info("=== Login-Versuch in CurrentUser ===");
            LOGGER.info("Benutzername: " + username);
            LOGGER.info("Rohes Passwort (Länge): " + password.length());
            
            String passHash = hashPassword(password);
            LOGGER.info("Generierter Hash: " + passHash);
            LOGGER.info("Hash-Länge: " + passHash.length());
            
            user = userDAO.isAdminOrClient(username, passHash);
            
            if (user != null) {
                LOGGER.info("Login erfolgreich für: " + username);
            } else {
                LOGGER.warning("Login fehlgeschlagen für: " + username);
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
            LOGGER.info("Password hashing details:");
            LOGGER.info("Input password length: " + password.length());
            LOGGER.info("Generated hash: " + result);
            LOGGER.info("Hash length: " + result.length());
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
