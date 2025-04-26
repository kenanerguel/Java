package com.mein.projekt.dao;

import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Named
@ApplicationScoped
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";
    
    @Inject
    private EntityManagerProvider entityManagerProvider;

    // Standardkonstruktor für CDI
    public UserDAO() {
        LOGGER.info("UserDAO wurde mit Standard-Konstruktor erstellt");
    }
    
    // Konstruktor für manuelle Instanziierung (z.B. in main Methode)
    public UserDAO(EntityManagerProvider provider) {
        LOGGER.info("UserDAO wurde mit EntityManagerProvider erstellt");
        this.entityManagerProvider = provider;
    }
    
    public void setEntityManagerProvider(EntityManagerProvider provider) {
        if (provider == null) {
            LOGGER.severe("EntityManagerProvider ist null");
            throw new IllegalArgumentException("EntityManagerProvider darf nicht null sein");
        }
        this.entityManagerProvider = provider;
        LOGGER.info("EntityManagerProvider erfolgreich gesetzt");
    }

    /**
     * Speichert einen neuen Benutzer in der Datenbank.
     */
    public void saveUser(User user) {
        EntityManager em = entityManagerProvider.getEntityManager();
        if (em == null) {
            LOGGER.severe("EntityManager ist null - konnte nicht initialisiert werden");
            return;
        }
        
        // Hash the password before saving
        user.setPassword(hashPassword(user.getUsername(), user.getPassword()));
        
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(user);
            transaction.commit();
            LOGGER.info("Benutzer " + user.getUsername() + " erfolgreich gespeichert");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Benutzers", e);
        }
    }

    /**
     * Überprüft, ob ein Benutzer Admin oder Client ist.
     */
    public User isAdminOrClient(String username, String password) {
        EntityManager em = entityManagerProvider.getEntityManager();
        if (em == null) {
            LOGGER.severe("EntityManager ist null - konnte nicht initialisiert werden");
            return null;
        }

        try {
            LOGGER.info("Versuche Benutzer zu finden: " + username);
            
            // First, try to find the user
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username",
                User.class);
            query.setParameter("username", username);
            
            User user = null;
            try {
                user = query.getSingleResult();
                LOGGER.info("Benutzer gefunden: " + username);
                
                // Hash the provided password for comparison
                String hashedInputPassword = hashPassword(username, password);
                LOGGER.info("Gespeicherter Hash: " + user.getPassword());
                LOGGER.info("Berechneter Hash: " + hashedInputPassword);
                
                // Compare the hashed passwords
                if (user.getPassword().equals(hashedInputPassword)) {
                    LOGGER.info("Benutzer gefunden und authentifiziert: " + username);
                    return user;
                }
            } catch (NoResultException e) {
                LOGGER.warning("Kein Benutzer gefunden für: " + username);
                return null;
            }
            
            LOGGER.warning("Passwort falsch für Benutzer: " + username);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Suchen des Benutzers: " + username, e);
            return null;
        }
    }

    /**
     * Berechnet den Hash für ein Passwort unter Verwendung des Benutzernamens und eines Salts.
     */
    protected String hashPassword(String username, String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digester.digest((username + password + salt).getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Hashen des Passworts", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Main-Methode für Tests
     */
    public static void main(String[] args) {
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        UserDAO userDAO = new UserDAO(entityManagerProvider);

        // Test password hash
        String testPassword = "admin123";
        String username = "admin";
        String generatedHash = userDAO.hashPassword(username, testPassword);
        System.out.println("Password verification test:");
        System.out.println("Username: " + username);
        System.out.println("Password: " + testPassword);
        System.out.println("Generated hash: " + generatedHash);
        
        // Test-Logik: Admin/Client prüfen
        System.out.println("\nadmin isAdmin? " + userDAO.isAdminOrClient("admin", "admin123"));
        System.out.println("science1 isClient? " + userDAO.isAdminOrClient("science1", "pass123"));
    }
}

