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
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";
    
    @Inject
    private EntityManagerProvider entityManagerProvider;
    
    @Inject
    private UserDAO userDAO;
    
    private User user = null;
    
    public CurrentUser() {
        LOGGER.finest("CurrentUser wurde erstellt");
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

    /**
     * Handhabt die Authentifizierung eines Benutzers anhand von Benutzername und Passwort.
     */
    public void handleUser(String username, String password) {
        try {
            LOGGER.info("Login-Versuch für Benutzer: " + username);
            String passHash = hashPassword(password);
            user = userDAO.isAdminOrClient(username, passHash);
            
            if (user != null) {
                LOGGER.info("Login erfolgreich für: " + username);
            } else {
                LOGGER.info("Login fehlgeschlagen für: " + username);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Login", e);
            user = null;
        }
    }

    /**
     * Überprüft, ob der Benutzer gültig ist (Admin oder Client).
     */
    public boolean isValid() {
        return user != null;
    }

    /**
     * Setzt den Benutzerstatus zurück (z. B. beim Logout).
     */
    public void reset() {
        this.user = null;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Hashen des Passworts", e);
            throw new RuntimeException(e);
        }
    }

    public User getUser() {
        return this.user;
    }
}
