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
    private static final String PERSISTENCE_UNIT_NAME = "likeHeroPU";
    
    private EntityManagerFactory emf;
    private EntityManager em;
    private boolean initialized = false;
    
    public EntityManagerProvider() {
        LOGGER.log(Level.INFO, "EntityManagerProvider wurde instanziiert");
    }
    
    public void init() {
        if (!initialized) {
            try {
                LOGGER.log(Level.INFO, "Initialisiere EntityManagerFactory mit {0}", PERSISTENCE_UNIT_NAME);
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                LOGGER.log(Level.INFO, "EntityManagerFactory wurde erfolgreich initialisiert");
                
                LOGGER.log(Level.INFO, "Erstelle EntityManager");
                em = emf.createEntityManager();
                LOGGER.log(Level.INFO, "EntityManager wurde erfolgreich erstellt");
                
                initialized = true;
                LOGGER.log(Level.INFO, "EntityManagerProvider wurde erfolgreich initialisiert");
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
        if (em == null || !em.isOpen()) {
            LOGGER.log(Level.INFO, "EntityManager ist geschlossen oder null, erstelle einen neuen");
            em = emf.createEntityManager();
        }
        return em;
    }
    
    public void close() {
        cleanup();
    }
    
    private void cleanup() {
        initialized = false;
        
        if (em != null && em.isOpen()) {
            try {
                LOGGER.log(Level.INFO, "Schließe EntityManager");
                em.close();
                LOGGER.log(Level.INFO, "EntityManager wurde geschlossen");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Fehler beim Schließen des EntityManagers", e);
            } finally {
                em = null;
            }
        }
        
        if (emf != null && emf.isOpen()) {
            try {
                LOGGER.log(Level.INFO, "Schließe EntityManagerFactory");
                emf.close();
                LOGGER.log(Level.INFO, "EntityManagerFactory wurde geschlossen");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Fehler beim Schließen der EntityManagerFactory", e);
            } finally {
                emf = null;
            }
        }
    }
}
