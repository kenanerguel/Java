package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
@Path("/auth")
public class LoginDiagnostic {
    private static final Logger LOGGER = Logger.getLogger(LoginDiagnostic.class.getName());
    
    @Inject
    protected EntityManagerProvider entityManagerProvider;
    
    @Inject
    protected UserDAO userDAO;
    
    // Setter für Tests
    public void setEntityManagerProvider(EntityManagerProvider provider) {
        this.entityManagerProvider = provider;
    }
    
    public void setUserDAO(UserDAO dao) {
        this.userDAO = dao;
    }
    
    @POST
    @Path("/diagnose")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response diagnoseLoginEndpoint(@FormParam("username") String username, @FormParam("password") String password) {
        try {
            LoginDiagnosticResult result = performDiagnosis(username, password);
            return Response.ok(result).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler bei der Login-Diagnose", e);
            return Response.serverError().entity("Fehler bei der Login-Diagnose: " + e.getMessage()).build();
        }
    }
    
    /**
     * Führt eine vollständige Diagnose des Login-Prozesses durch
     * @param username Der zu testende Benutzername
     * @param password Das zu testende Passwort
     * @return Ein Diagnose-Ergebnis mit detaillierten Informationen
     */
    private LoginDiagnosticResult performDiagnosis(String username, String password) {
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
    
    public static class LoginDiagnosticResult {
        private boolean databaseConnection;
        private boolean userExists;
        private boolean passwordHashing;
        private boolean loginAttempt;
        private String message;
        
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
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        @Override
        public String toString() {
            return "LoginDiagnosticResult{" +
                   "databaseConnection=" + databaseConnection +
                   ", userExists=" + userExists +
                   ", passwordHashing=" + passwordHashing +
                   ", loginAttempt=" + loginAttempt +
                   ", message='" + message + '\'' +
                   '}';
        }
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
} 