package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class LoginDiagnostic {
    private static final Logger LOGGER = Logger.getLogger(LoginDiagnostic.class.getName());
    
    @Inject
    EntityManagerProvider entityManagerProvider;
    
    @Inject
    UserDAO userDAO;
    
    /**
     * Führt eine vollständige Diagnose des Login-Prozesses durch
     * @param username Der zu testende Benutzername
     * @param password Das zu testende Passwort
     * @return Ein Diagnose-Ergebnis mit detaillierten Informationen
     */
    public LoginDiagnosticResult diagnoseLogin(String username, String password) {
        LoginDiagnosticResult result = new LoginDiagnosticResult();
        
        // 1. Überprüfe Datenbankverbindung
        result.setDatabaseConnection(checkDatabaseConnection());
        
        // 2. Überprüfe Benutzer in der Datenbank
        result.setUserExists(checkUserExists(username));
        
        // 3. Überprüfe Passwort-Hashing
        result.setPasswordHashing(checkPasswordHashing(username, password));
        
        // 4. Überprüfe Login-Versuch
        result.setLoginAttempt(checkLoginAttempt(username, password));
        
        return result;
    }
    
    private boolean checkDatabaseConnection() {
        try {
            EntityManager em = entityManagerProvider.getEntityManager();
            if (em == null) {
                LOGGER.severe("EntityManager ist null");
                return false;
            }
            
            // Teste die Verbindung mit einer einfachen Abfrage
            em.createNativeQuery("SELECT 1").getSingleResult();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler bei der Datenbankverbindung", e);
            return false;
        }
    }
    
    private boolean checkUserExists(String username) {
        try {
            EntityManager em = entityManagerProvider.getEntityManager();
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            
            User user = query.getSingleResult();
            return user != null;
        } catch (NoResultException e) {
            LOGGER.info("Benutzer nicht gefunden: " + username);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Überprüfen des Benutzers", e);
            return false;
        }
    }
    
    private boolean checkPasswordHashing(String username, String password) {
        try {
            String hashedPassword = CurrentUser.hashPassword(username, password);
            return hashedPassword != null && !hashedPassword.isEmpty();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Passwort-Hashing", e);
            return false;
        }
    }
    
    private boolean checkLoginAttempt(String username, String password) {
        try {
            User user = userDAO.isAdminOrClient(username, CurrentUser.hashPassword(username, password));
            return user != null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Login-Versuch", e);
            return false;
        }
    }
    
    /**
     * Klasse zur Speicherung der Diagnose-Ergebnisse
     */
    public static class LoginDiagnosticResult {
        private boolean databaseConnection;
        private boolean userExists;
        private boolean passwordHashing;
        private boolean loginAttempt;
        
        public boolean isDatabaseConnection() {
            return databaseConnection;
        }
        
        public void setDatabaseConnection(boolean databaseConnection) {
            this.databaseConnection = databaseConnection;
        }
        
        public boolean isUserExists() {
            return userExists;
        }
        
        public void setUserExists(boolean userExists) {
            this.userExists = userExists;
        }
        
        public boolean isPasswordHashing() {
            return passwordHashing;
        }
        
        public void setPasswordHashing(boolean passwordHashing) {
            this.passwordHashing = passwordHashing;
        }
        
        public boolean isLoginAttempt() {
            return loginAttempt;
        }
        
        public void setLoginAttempt(boolean loginAttempt) {
            this.loginAttempt = loginAttempt;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Login-Diagnose-Ergebnis:\n");
            sb.append("Datenbankverbindung: ").append(databaseConnection ? "OK" : "FEHLER").append("\n");
            sb.append("Benutzer existiert: ").append(userExists ? "JA" : "NEIN").append("\n");
            sb.append("Passwort-Hashing: ").append(passwordHashing ? "OK" : "FEHLER").append("\n");
            sb.append("Login-Versuch: ").append(loginAttempt ? "ERFOLGREICH" : "FEHLGESCHLAGEN").append("\n");
            return sb.toString();
        }
    }
} 