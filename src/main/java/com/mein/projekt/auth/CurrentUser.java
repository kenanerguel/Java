package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Named
@SessionScoped
public class CurrentUser implements Serializable {

    private final UserDAO userDAO;
    private User user = null;

    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";

    public CurrentUser() {
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        this.userDAO = new UserDAO(entityManagerProvider);
    }

    /**
     * Handhabt die Authentifizierung eines Benutzers anhand von Benutzername und Passwort.
     */
    public void handleUser(String username, String password) {
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

    private static String hashPassword(String name, String pass, String salt) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digester.digest((name + pass + salt).getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void reset() {
        this.user = null;
    }

    public User getUser() {
        return this.user;
    }
}
