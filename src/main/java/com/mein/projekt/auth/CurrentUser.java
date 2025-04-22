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
    
    private UserDAO userDAO;
    private User user = null;
    
    public CurrentUser() {
        // Leerer Konstruktor für CDI
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

    /**
     * Handhabt die Authentifizierung eines Benutzers anhand von Benutzername und Passwort.
     */
    public void handleUser(String username, String password) {
        if (userDAO == null) {
            LOGGER.severe("UserDAO ist null. Initialisiere es jetzt...");
            this.userDAO = new UserDAO();
            this.userDAO.setEntityManagerProvider(entityManagerProvider);
        }
        
        String passHash = hashPassword(username, password, salt);
        user = userDAO.isAdminOrClient(username, passHash);
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

    private static String hashPassword(String name, String pass, String salt) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digester.digest((name + pass + salt).getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser() {
        return this.user;
    }
}
