package com.mein.projekt.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.logging.Logger;

@ApplicationScoped
public class EntityManagerProducer {
    
    private static final Logger LOGGER = Logger.getLogger(EntityManagerProducer.class.getName());
    private static EntityManagerFactory emf;
    
    static {
        try {
            emf = Persistence.createEntityManagerFactory("co2PU");
            LOGGER.info("EntityManagerFactory wurde erfolgreich erstellt");
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Erstellen der EntityManagerFactory: " + e.getMessage());
            throw new RuntimeException("Konnte EntityManagerFactory nicht erstellen", e);
        }
    }
    
    @Produces
    public EntityManager createEntityManager() {
        try {
            EntityManager em = emf.createEntityManager();
            LOGGER.info("Neuer EntityManager wurde erstellt");
            return em;
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Erstellen des EntityManager: " + e.getMessage());
            throw new RuntimeException("Konnte EntityManager nicht erstellen", e);
        }
    }
} 