package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class DatabaseTest {
    private static final Logger LOGGER = Logger.getLogger(DatabaseTest.class.getName());
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";
    
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
            
            // Test admin login
            LOGGER.info("\nTeste Admin Login...");
            UserDAO userDAO = new UserDAO(entityManagerProvider);
            String adminUsername = "admin";
            String adminPassword = "admin123";
            String adminHash = hashPassword(adminUsername, adminPassword);
            
            LOGGER.info("Versuche Admin Login mit:");
            LOGGER.info("Username: " + adminUsername);
            LOGGER.info("Password: " + adminPassword);
            
            User adminUser = userDAO.isAdminOrClient(adminUsername, adminHash);
            if (adminUser != null) {
                LOGGER.info("Admin Login erfolgreich!");
                LOGGER.info("Admin Details:");
                LOGGER.info("- ID: " + adminUser.getId());
                LOGGER.info("- Username: " + adminUser.getUsername());
                LOGGER.info("- Is Admin: " + adminUser.isAdmin());
            } else {
                LOGGER.warning("Admin Login fehlgeschlagen!");
            }
            
            // Test scientist login
            LOGGER.info("\nTeste Scientist Login...");
            String scientistUsername = "science1";
            String scientistPassword = "science123";
            String scientistHash = hashPassword(scientistUsername, scientistPassword);
            
            LOGGER.info("Versuche Scientist Login mit:");
            LOGGER.info("Username: " + scientistUsername);
            LOGGER.info("Password: " + scientistPassword);
            
            User scientistUser = userDAO.isAdminOrClient(scientistUsername, scientistHash);
            if (scientistUser != null) {
                LOGGER.info("Scientist Login erfolgreich!");
                LOGGER.info("Scientist Details:");
                LOGGER.info("- ID: " + scientistUser.getId());
                LOGGER.info("- Username: " + scientistUser.getUsername());
                LOGGER.info("- Is Admin: " + scientistUser.isAdmin());
            } else {
                LOGGER.warning("Scientist Login fehlgeschlagen!");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Testen der Datenbank", e);
        }
    }
    
    private static String hashPassword(String username, String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digester.digest((username + password + salt).getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Hashen des Passworts: " + e.getMessage());
            throw new RuntimeException("Passwort-Hashing fehlgeschlagen", e);
        }
    }
} 