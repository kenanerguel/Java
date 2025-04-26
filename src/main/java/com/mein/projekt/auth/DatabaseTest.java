package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseTest {
    private static final Logger LOGGER = Logger.getLogger(DatabaseTest.class.getName());
    
    public static void main(String[] args) {
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        entityManagerProvider.init();
        
        EntityManager em = entityManagerProvider.getEntityManager();
        if (em == null) {
            LOGGER.severe("EntityManager konnte nicht erstellt werden");
            return;
        }
        
        try {
            // Test database connection
            LOGGER.info("Teste Datenbankverbindung...");
            Query testQuery = em.createNativeQuery("SELECT 1");
            Object result = testQuery.getSingleResult();
            LOGGER.info("Datenbankverbindung erfolgreich: " + result);
            
            // Check if users table exists
            LOGGER.info("Prüfe users Tabelle...");
            Query tableQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = 'co2db' AND table_name = 'users'");
            Long tableCount = ((Number) tableQuery.getSingleResult()).longValue();
            LOGGER.info("Users Tabelle existiert: " + (tableCount > 0));
            
            // Count users
            if (tableCount > 0) {
                Query countQuery = em.createNativeQuery("SELECT COUNT(*) FROM users");
                Long userCount = ((Number) countQuery.getSingleResult()).longValue();
                LOGGER.info("Anzahl Benutzer in der Datenbank: " + userCount);
                
                // Check science1 user specifically
                Query userQuery = em.createNativeQuery(
                    "SELECT * FROM users WHERE username = 'science1'");
                try {
                    Object[] userData = (Object[]) userQuery.getSingleResult();
                    LOGGER.info("science1 Benutzer gefunden:");
                    LOGGER.info("ID: " + userData[0]);
                    LOGGER.info("Username: " + userData[3]);
                    LOGGER.info("Password Hash: " + userData[2]);
                    LOGGER.info("Is Admin: " + userData[1]);
                } catch (Exception e) {
                    LOGGER.warning("science1 Benutzer nicht gefunden");
                }
            }
            
            // Test user authentication
            LOGGER.info("\nTeste Benutzerauthentifizierung...");
            UserDAO userDAO = new UserDAO(entityManagerProvider);
            String username = "science1";
            String password = "pass123";
            String hashedPassword = CurrentUser.hashPassword(username, password);
            
            LOGGER.info("Versuche Login mit:");
            LOGGER.info("Username: " + username);
            LOGGER.info("Password Hash: " + hashedPassword);
            
            User user = userDAO.isAdminOrClient(username, hashedPassword);
            if (user != null) {
                LOGGER.info("Login erfolgreich!");
                LOGGER.info("User Details:");
                LOGGER.info("- ID: " + user.getId());
                LOGGER.info("- Username: " + user.getUsername());
                LOGGER.info("- Is Admin: " + user.isAdmin());
                LOGGER.info("- Stored Hash: " + user.getPassword());
            } else {
                LOGGER.warning("Login fehlgeschlagen!");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Testen der Datenbank", e);
        }
    }
} 