package com.mein.projekt.dao;

import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    
    private EntityManager entityManager;
    private EntityManagerProvider entityManagerProvider;

    // Standardkonstruktor für CDI
    public UserDAO() {
        LOGGER.info("UserDAO wurde mit Standard-Konstruktor erstellt");
    }
    
    @Inject
    public UserDAO(EntityManagerProvider entityManagerProvider) {
        this.setEntityManagerProvider(entityManagerProvider);
        LOGGER.info("UserDAO wurde mit EntityManagerProvider erstellt");
    }
    
    public void setEntityManagerProvider(EntityManagerProvider provider) {
        try {
            this.entityManagerProvider = provider;
            this.entityManager = provider.getEntityManager();
            LOGGER.info("EntityManager erfolgreich gesetzt");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Setzen des EntityManagers", e);
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
     * Überprüft, ob ein Benutzer Client (nicht Admin) ist.
     * Beispielhafte Abfrage: SELECT u FROM User u WHERE ...
     * Hier wird angenommen, dass User ein Feld "admin" hat,
     * das angibt, ob er Admin ist oder nicht.
     * Überprüft, ob ein Benutzer Admin oder Client ist.
     */
    public User isAdminOrClient(String username, String password) {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - konnte nicht initialisiert werden");
            return null;
        }
        
        try {
            LOGGER.info("Suche Benutzer mit Username: " + username);
            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.username = :uname AND u.password = :pwd",
                            User.class)
                    .setParameter("uname", username)
                    .setParameter("pwd", password)
                    .getSingleResult();

            // Falls user != null, gib zurück, ob er Admin ist.
            LOGGER.info("Benutzer gefunden: " + username + ", ist Admin: " + user.isAdmin());
            return user;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Kein Benutzer gefunden oder Fehler bei der Suche", e);
            return null;  // Falls kein Nutzer gefunden oder Fehler
        }
    }

    /**
     * Main-Methode für Tests
     */
    public static void main(String[] args) {
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        UserDAO userDAO = new UserDAO(entityManagerProvider);

        // Beispiel-User speichern
        /*
        User adminUser = new User("root", "root", true);
        User clientUser1 = new User("Wissenschaftler_1", "Hallo", false);
        User clientUser2 = new User("Wissenschaftler_2", "Servus", false);


        userDAO.saveUser(adminUser);
        userDAO.saveUser(clientUser1);
        userDAO.saveUser(clientUser2);
        */

        // Test-Logik: Admin/Client prüfen
        System.out.println("root isAdmin? " + userDAO.isAdminOrClient("root", "dXRcRLCMz+kUxl9QORmRxyPfliMK/6hF1zild9sVmuu4BHCemIAfqAH8GXjbomZAFmjdAJ0F6nESJhDjCraIRQ==")); // sollte true sein
        System.out.println("Wissenschaftler_1 isClient? " + userDAO.isAdminOrClient("Wissenschaftler_1", "+sasj0x49+vlfzERhRBCRHazOgEOo2WWljaclLTWv9M7xdYJvX0g0hAxFWhErpEebMCQox83NijXNi4AoC3Jhw==")); // sollte true sein
    }
}

