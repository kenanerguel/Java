package com.mein.projekt.dao;

import com.mein.projekt.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    
    @Inject
    private EntityManager entityManager;

    public UserDAO() {
        LOGGER.info("UserDAO wurde mit Standard-Konstruktor erstellt");
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
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Benutzers", e);
        }
    }

    /**
     * Überprüft die Anmeldedaten eines Benutzers.
     */
    public User isAdminOrClient(String username, String password) {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - konnte nicht initialisiert werden");
            return null;
        }
        
        try {
            LOGGER.info("Login-Versuch für Benutzer: " + username);
            
            User foundUser = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :uname", User.class)
                .setParameter("uname", username)
                .getSingleResult();
            
            if (foundUser != null && foundUser.getPassword().equals(password)) {
                LOGGER.info("Login erfolgreich für Benutzer: " + username);
                return foundUser;
            }
            
            LOGGER.info("Login fehlgeschlagen - Falsches Passwort für: " + username);
            return null;
            
        } catch (NoResultException e) {
            LOGGER.info("Login fehlgeschlagen - Kein Benutzer gefunden mit Username: " + username);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Login fehlgeschlagen - Unerwarteter Fehler", e);
            return null;
        }
    }

    /**
     * Main-Methode für Tests
     */
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

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

