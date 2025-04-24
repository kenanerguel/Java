package com.mein.projekt.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class EntityManagerProvider implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EntityManagerProvider.class.getName());
    private static final String PERSISTENCE_UNIT_NAME = "co2PU";
    
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private boolean initialized = false;
    
    public EntityManagerProvider() {
        LOGGER.info("EntityManagerProvider wurde instanziiert");
    }
    
    public void init() {
        if (!initialized) {
            try {
                LOGGER.info("Initialisiere EntityManagerFactory mit " + PERSISTENCE_UNIT_NAME);
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                entityManager = entityManagerFactory.createEntityManager();
                LOGGER.info("EntityManagerFactory erfolgreich initialisiert");
                
                initialized = true;
                LOGGER.info("EntityManagerProvider wurde erfolgreich initialisiert");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Fehler bei der Initialisierung von EntityManagerProvider", e);
                cleanup();
                throw new RuntimeException("Fehler bei der Initialisierung des Persistence-Systems", e);
            }
        }
    }
    
    public EntityManager getEntityManager() {
        if (!initialized) {
            init();
        }
        if (entityManager == null || !entityManager.isOpen()) {
            LOGGER.info("EntityManager ist geschlossen oder null, erstelle einen neuen");
            entityManager = entityManagerFactory.createEntityManager();
        }
        return entityManager;
    }
    
    public void closeEntityManager() {
        cleanup();
    }
    
    private void cleanup() {
        initialized = false;
        
        if (entityManager != null && entityManager.isOpen()) {
            try {
                LOGGER.info("Schließe EntityManager");
                entityManager.close();
                LOGGER.info("EntityManager wurde geschlossen");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Fehler beim Schließen des EntityManagers", e);
            } finally {
                entityManager = null;
            }
        }
        
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            try {
                LOGGER.info("Schließe EntityManagerFactory");
                entityManagerFactory.close();
                LOGGER.info("EntityManagerFactory wurde geschlossen");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Fehler beim Schließen der EntityManagerFactory", e);
            } finally {
                entityManagerFactory = null;
            }
        }
    }
}
