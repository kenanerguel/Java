package com.mein.projekt.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.logging.Logger;
import java.util.logging.Level;

@ApplicationScoped
public class EntityManagerProvider {
    
    private static final Logger LOGGER = Logger.getLogger(EntityManagerProvider.class.getName());
    private static EntityManagerFactory emf;
    private static EntityManager em;
    
    public EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            try {
                LOGGER.info("Initialisiere EntityManager...");
                if (emf == null || !emf.isOpen()) {
                    emf = Persistence.createEntityManagerFactory("likeHeroPU");
                }
                em = emf.createEntityManager();
                LOGGER.info("EntityManager erfolgreich initialisiert.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Fehler beim Initialisieren des EntityManagers", e);
                throw new RuntimeException("Fehler beim Initialisieren des EntityManagers: " + e.getMessage(), e);
            }
        }
        return em;
    }
    
    // Hilfsmethode zum Schließen der Ressourcen
    public static void close() {
        try {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Fehler beim Schließen der Ressourcen", e);
        }
    }
}
