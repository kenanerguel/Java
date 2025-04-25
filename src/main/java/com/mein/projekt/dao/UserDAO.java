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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.annotation.PostConstruct;
import org.mindrot.jbcrypt.BCrypt;

@Named
@ApplicationScoped
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    
    @Inject
    private EntityManagerProvider entityManagerProvider;
    
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;

    // Standardkonstruktor für CDI
    public UserDAO() {
        LOGGER.info("UserDAO wurde mit Standard-Konstruktor erstellt");
    }
    
    @PostConstruct
    public void init() {
        setEntityManagerProvider(entityManagerProvider);
    }
    
    // Konstruktor für manuelle Instanziierung (z.B. in main Methode)
    public UserDAO(EntityManagerProvider provider) {
        LOGGER.info("UserDAO wurde mit EntityManagerProvider erstellt");
        setEntityManagerProvider(provider);
    }
    
    public void setEntityManagerProvider(EntityManagerProvider provider) {
        try {
            if (provider == null) {
                LOGGER.severe("EntityManagerProvider ist null");
                throw new IllegalArgumentException("EntityManagerProvider darf nicht null sein");
            }
            this.entityManagerProvider = provider;
            this.entityManager = provider.getEntityManager();
            if (this.entityManager == null) {
                LOGGER.severe("EntityManager konnte nicht initialisiert werden");
                throw new IllegalStateException("EntityManager konnte nicht initialisiert werden");
            }
            LOGGER.info("EntityManager erfolgreich initialisiert");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Setzen des EntityManagers", e);
            throw new RuntimeException("Fehler beim Initialisieren des EntityManagers", e);
        }
    }

    /**
     * Speichert einen neuen Benutzer in der Datenbank.
     */
    public void saveUser(User user) {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - konnte nicht initialisiert werden");
            return;
        }
        
        // Hash the password before saving
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
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
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :username",
                User.class);
            query.setParameter("username", username);
            
            User user = query.getSingleResult();
            
            // Verify the password using BCrypt
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                LOGGER.info("Benutzer gefunden und authentifiziert: " + username);
                return user;
            }
            
            LOGGER.info("Passwort falsch für Benutzer: " + username);
            return null;
        } catch (NoResultException e) {
            LOGGER.info("Kein Benutzer gefunden für: " + username);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Suchen des Benutzers: " + username, e);
            return null;
        }
    }

    /**
     * Main-Methode für Tests
     */
    public static void main(String[] args) {
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        UserDAO userDAO = new UserDAO(entityManagerProvider);

        // Test-Logik: Admin/Client prüfen
        System.out.println("root isAdmin? " + userDAO.isAdminOrClient("root", "dXRcRLCMz+kUxl9QORmRxyPfliMK/6hF1zild9sVmuu4BHCemIAfqAH8GXjbomZAFmjdAJ0F6nESJhDjCraIRQ==")); // sollte true sein
        System.out.println("Wissenschaftler_1 isClient? " + userDAO.isAdminOrClient("Wissenschaftler_1", "+sasj0x49+vlfzERhRBCRHazOgEOo2WWljaclLTWv9M7xdYJvX0g0hAxFWhErpEebMCQox83NijXNi4AoC3Jhw==")); // sollte true sein
    }
}

