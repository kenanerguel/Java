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
    private final ThreadLocal<EntityManager> entityManager = new ThreadLocal<>();
    private boolean initialized = false;
    
    public EntityManagerProvider() {
        LOGGER.info("EntityManagerProvider wurde instanziiert");
    }
    
    public void init() {
        if (!initialized) {
            try {
                LOGGER.info("Initialisiere EntityManagerFactory mit " + PERSISTENCE_UNIT_NAME);
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                initialized = true;
                LOGGER.info("EntityManagerFactory erfolgreich initialisiert");
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
        
        EntityManager em = entityManager.get();
        if (em == null || !em.isOpen()) {
            em = entityManagerFactory.createEntityManager();
            entityManager.set(em);
            LOGGER.fine("Neuer EntityManager wurde erstellt");
        }
        return em;
    }
    
    public void closeEntityManager() {
        EntityManager em = entityManager.get();
        if (em != null && em.isOpen()) {
            try {
                em.close();
                LOGGER.fine("EntityManager wurde geschlossen");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Fehler beim Schließen des EntityManagers", e);
            } finally {
                entityManager.remove();
            }
        }
    }
    
    private void cleanup() {
        closeEntityManager();
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            try {
                entityManagerFactory.close();
                LOGGER.info("EntityManagerFactory wurde geschlossen");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Fehler beim Schließen der EntityManagerFactory", e);
            }
        }
        entityManagerFactory = null;
        initialized = false;
    }
}
